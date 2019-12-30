package routes.interactions

import enums.CallbackId.CREATE_REQUEST
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.request.receive
import io.ktor.response.respond
import kotlinx.coroutines.runBlocking
import routes.interactions.responses.IssueCreatingResponseBody
import routes.interactions.responses.MessagePostingResponseBody
import routes.interactions.responses.ViewOpenResponseBody
import routes.interactions.requests.InteractionRequestBody
import routes.interactions.utils.*
import secrets.JiraSecrets
import utils.JiraApiRequester
import utils.RequestProcessor
import utils.SlackApiRequester
import utils.parseJson

class InteractionsProcessor(call: ApplicationCall) : RequestProcessor(call) {

    companion object {
        private const val SLACK_API_DOMAIN = "https://slack.com/api"
        private const val VIEW_OPEN_URL = "$SLACK_API_DOMAIN/views.open"
        private const val VIEW_UPDATE_URL = "$SLACK_API_DOMAIN/views.update"
        private const val MESSAGE_POSTING_URL = "$SLACK_API_DOMAIN/chat.postMessage"
        private const val ISSUE_CREATING_URL = "${JiraSecrets.DOMAIN}/rest/api/2/issue"
        private const val BSSCCO_TEST_2_CHANNEL_ID = "CQ15ND811"
        private const val DATA_CHANNEL_ID = "CRXEVUQP9"
    }

    private val requestBody = getRequestBody()

    private fun getRequestBody(): InteractionRequestBody {
        return runBlocking {
            call.receive<Parameters>()["payload"]!!
//                .also { println(it) }
                .parseJson<InteractionRequestBody>()
        }
    }

    override suspend fun process() {
        if (isRequestCreatingRequest()) {
            respondAccepted()
            progressWithProgressModal {
                val issueKey = createRequestIssue()
                val messageTs = postRequestCreatedMessage(issueKey)
                appendMessageLinkToIssue(issueKey, messageTs)
            }
        }
    }

    private fun isRequestCreatingRequest(): Boolean {
        return requestBody.view.callbackId == CREATE_REQUEST.name.toLowerCase()
    }

    private suspend fun respondAccepted() {
        call.respond(status = HttpStatusCode.Accepted, message = "")
    }

    private suspend fun progressWithProgressModal(progress: suspend () -> Unit) {
        val modalViewId = openProgressModal()
        progress.invoke()
        updateWithCompleteModal(modalViewId)
    }

    private suspend fun openProgressModal(): String {
        val json = ProgressModalJsonCreator.create(requestBody.triggerId)
        return SlackApiRequester.post<ViewOpenResponseBody>(VIEW_OPEN_URL, json).view.id
    }

    private suspend fun createRequestIssue(): String {
        val json = RequestIssueJsonCreator(requestBody).createJson()
        return JiraApiRequester.post<IssueCreatingResponseBody>(ISSUE_CREATING_URL, json).key
    }

    private suspend fun postRequestCreatedMessage(issueKey: String): String {
        val json = RequestCreatedMessageJsonCreator(requestBody, getSlackChannelId(), issueKey).create()
        return SlackApiRequester.post<MessagePostingResponseBody>(MESSAGE_POSTING_URL, json).ts
    }

    private fun getSlackChannelId(): String {
        requestBody.view.state.values.run {
            if (requestDescription.action.value == "tttt") return BSSCCO_TEST_2_CHANNEL_ID
            return DATA_CHANNEL_ID
        }
    }

    private suspend fun appendMessageLinkToIssue(issueKey: String, messageTs: String) {
        val description = RequestIssueJsonCreator(requestBody).createDescription()
        val json = MessageLinkAppendedDescriptionJsonCreator(getSlackChannelId(), messageTs, description).create()
        JiraApiRequester.put<Unit>(getIssueApiUrl(issueKey), json)
    }

    private fun getIssueApiUrl(issueKey: String): String {
        return "${JiraSecrets.DOMAIN}/rest/api/2/issue/$issueKey"
    }

    private suspend fun updateWithCompleteModal(viewId: String) {
        SlackApiRequester.post<Unit>(VIEW_UPDATE_URL, CompleteModalJsonCreator.create(viewId))
    }
}