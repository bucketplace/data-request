package routes.interactions.utils

import db.members.MemberDao
import enums.RequestType
import routes.interactions.requests.InteractionRequestBody
import utils.convertUtf8mb4
import utils.escapeNewLine
import utils.toJson
import kotlin.math.min

@Suppress("SimpleRedundantLet")
class RequestIssueJsonCreator(
    requestBody: InteractionRequestBody
) {

    companion object {
        private const val PROJECT_FIELD_BUCKETPLACE_DATA_ID = "10705"
        private const val ISSUE_FIELD_BUG_TYPE_ID = "10101"
    }

    private val hopeResponsiblePerson: String
    private val hopeDueDate: String
    private val requestType: RequestType
    private val requestSubject: String
    private val requestDescription: String
    private val aggregaionPeriodStart: String
    private val aggregaionPeriodEnd: String
    private val aggregaionPeriodUnit: String
    private val aggregationPlatform: String
    private val requesterNickname: String

    init {
        val submissionValues = requestBody.view.state.values
        hopeResponsiblePerson = submissionValues.hopeResponsiblePerson.action.selectedOption?.value ?: "-"
        hopeDueDate = submissionValues.hopeDueDate.action.selectedDate!!
        requestType = RequestType.get(submissionValues.requestType.action.selectedOption!!.value)
        requestSubject = submissionValues.requestSubject.action.value!!
        requestDescription = submissionValues.requestDescription.action.value!!
        aggregaionPeriodStart = submissionValues.aggregationPeriodStart.action.selectedDate!!
        aggregaionPeriodEnd = submissionValues.aggregationPeriodEnd.action.selectedDate!!
        aggregaionPeriodUnit = submissionValues.aggregationPeriodUnit.action.selectedOption!!.value
        aggregationPlatform = submissionValues.aggregationPlatform.action.selectedOptions!!.joinToString { it.value }
        requesterNickname = MemberDao().getMember(requestBody.user.id).profile!!.displayName!!
    }

    fun createJson() = """
        {
            "fields": {
                "project": { 
                    "id": "$PROJECT_FIELD_BUCKETPLACE_DATA_ID"
                },
                "issuetype": { 
                    "id": "$ISSUE_FIELD_BUG_TYPE_ID"
                },
                "summary": "${createSummary()}",
                "customfield_11216": ["$requesterNickname"],
                "customfield_11217": ["$hopeResponsiblePerson"],
                "customfield_11218": "$hopeDueDate",
                "customfield_11219": {
                    "id": "${requestType.jiraFieldValueId}"
                },
                "description": "${createDescription()}"
            }
        }
    """.toJson()

    private fun createSummary(): String {
        return requestSubject.convertUtf8mb4()
            .let { it.replace("\n", " ") }
            .let { it.substring(0, min(it.length, 100)) }
    }

    fun createDescription(): String {
        return buildString {
            append("\nh2. 요청 제목\n\n$requestSubject")
            append("\nh2. 요청 상세\n\n$requestDescription")
            append("\nh2. 집계 기간\n\n$aggregaionPeriodStart ~ $aggregaionPeriodEnd")
            append("\nh2. 집계 기준 단위\n\n$aggregaionPeriodUnit")
            append("\nh2. 집계 플랫폼\n\n$aggregationPlatform")
        }
            .convertUtf8mb4()
            .escapeNewLine()
    }
}