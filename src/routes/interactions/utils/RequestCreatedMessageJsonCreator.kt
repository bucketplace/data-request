package routes.interactions.utils

import db.members.MemberDao
import routes.interactions.requests.InteractionRequestBody
import secrets.JiraSecrets
import utils.SlackJsonCreator.createMarkdownText
import utils.escapeNewLine
import utils.toJson
import java.text.SimpleDateFormat
import java.util.*

class RequestCreatedMessageJsonCreator(
    private val interactionRequestBody: InteractionRequestBody,
    private val slackChannelId: String,
    private val issueKey: String
) {

    fun create() = """
        {
            "channel": "$slackChannelId",
            "blocks": [
                ${createFieldsSection()}
            ]
        }
    """.toJson()

    private fun createFieldsSection(): String {
        val submissionValues = interactionRequestBody.view.state.values
        return """
            {
                "type": "section",
                "text": ${createMarkdownText(
            StringBuffer().apply {
                append(createField("요청자", "<@${interactionRequestBody.user.id}>"))
                append(createField("요청 날짜", SimpleDateFormat("YYYY-MM-dd").format(Date())))
                append(createField("희망 담당자", getHopeResponsiblePerson()))
                append(createField("희망 완료일", submissionValues.hopeDueDate.action.selectedDate!!))
                append(createField("요청 유형", submissionValues.requestType.action.selectedOption!!.value))
                append(createField("요청 제목", submissionValues.requestSubject.action.value!!))
                append(createField("요청 상세", submissionValues.requestDescription.action.value!!))
                append(
                    createField(
                        "집계 기간",
                        "${submissionValues.aggregationPeriodStart.action.selectedDate!!} ~ ${submissionValues.aggregationPeriodEnd.action.selectedDate!!}"
                    )
                )
                append(createField("집계 기간 단위", submissionValues.aggregationPeriodUnit.action.selectedOption!!.value))
                append(createField("집계 플랫폼", submissionValues.aggregationPlatform.action.selectedOptions!!.joinToString { it.value }))
                append(createField("지라 링크", getIssueUrl(issueKey)))
            }.toString()
                .escapeNewLine()
        )}
            }
        """
    }

    private fun getHopeResponsiblePerson(): String {
        return interactionRequestBody.view.state.values.hopeResponsiblePerson.action.selectedOption
            ?.let { "<@${MemberDao().getMemberByNickname(it.value).id!!}>" }
            ?: "-"
    }

    private fun createField(label: String, text: String) = "\n*$label*\n$text"

    private fun getIssueUrl(issueKey: String): String {
        return "${JiraSecrets.DOMAIN}/browse/$issueKey"
    }
}