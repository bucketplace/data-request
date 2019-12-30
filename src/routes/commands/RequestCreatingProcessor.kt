package routes.commands

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode.Companion.Accepted
import io.ktor.http.Parameters
import io.ktor.request.receive
import io.ktor.response.respond
import kotlinx.coroutines.runBlocking
import routes.commands.utils.RequestFormModalJsonCreator
import utils.RequestProcessor
import utils.SlackApiRequester

class RequestCreatingProcessor(call: ApplicationCall) : RequestProcessor(call) {

    companion object {
        private const val VIEW_OPEN_URL = "https://slack.com/api/views.open"
    }

    private val triggerId = getTriggerId()

    private fun getTriggerId(): String {
        return runBlocking {
            call.receive<Parameters>()["trigger_id"]!!
        }
    }

    override suspend fun process() {
        respondAccepted()
        openFormModal()
    }

    private suspend fun respondAccepted() {
        call.respond(status = Accepted, message = "")
    }

    private suspend fun openFormModal() {
        SlackApiRequester.post<Unit>(VIEW_OPEN_URL, RequestFormModalJsonCreator.create(triggerId))
    }
}