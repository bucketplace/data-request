package routes.commands.utils

import enums.*
import utils.SlackJsonCreator.createDivider
import utils.SlackJsonCreator.createInputBlock
import utils.SlackJsonCreator.createMarkdownText
import utils.SlackJsonCreator.createOption
import utils.SlackJsonCreator.createPlainText
import utils.SlackJsonCreator.createSelectBlock
import utils.escapeNewLine
import utils.toJson

object RequestFormModalJsonCreator {

    fun create(triggerId: String) = """
        {
            "trigger_id": "$triggerId",
            "view": ${createView()} 
        }
    """.toJson()

    private fun createView() = """
        {
            "type": "modal",
            "callback_id": "${CallbackId.CREATE_REQUEST.name.toLowerCase()}",
            "title": ${createPlainText("데이터 요청")},
            "submit": ${createPlainText("등록")},
            "close": ${createPlainText("취소")},
            "blocks": [
                ${createUsageGuideBlock()},
                ${createDivider()},
                ${createHopeResponsiblePersonSelectBlock()},
                ${createHopeDueDatePickerBlock()},
                ${createRequestTypeSelectBlock()},
                ${createRequestSubjectInputBlock()},
                ${createRequestDescriptionInputBlock()},
                ${createAggregationPeriodStartDatePickerBlock()},
                ${createAggregationPeriodEndDatePickerBlock()},
                ${createAggregationPeriodUnitSelectBlock()},
                ${createAggregationPlatformMultiSelectBlock()}
            ]
        }
    """

    private fun createUsageGuideBlock() = """
        {
			"type": "section",
			"text": ${createMarkdownText("최소 영업일 +3일의 여유를 두고 요청\n에포트가 많이 들어가는 분석의 경우는 팀장과 협의 후 요청\n담당자, 유형, 완료일은 분석가들의 판단에 따라 지정한 내용에서 변경될 수 있음".escapeNewLine())}
		}
    """

    private fun createHopeResponsiblePersonSelectBlock(): String {
        return createSelectBlock(
            blockId = BlockId.HOPE_RESPONSIBLE_PERSON.name.toLowerCase(),
            label = createPlainText("희망 담당자"),
            placeholder = createPlainText(" "),
            options = createHopeResponsiblePersonOptions(),
            hint = createPlainText("진행을 희망하는 담당자 (사전에 논의되었거나, 팀내 분석가가 있는경우 지정)"),
            optional = true
        )
    }

    private fun createHopeResponsiblePersonOptions(): String {
        return HopeResponsiblePerson.values().toList()
            .map { createOption(it.name, it.name) }
            .toString()
    }

    private fun createHopeDueDatePickerBlock() = """
        {
            "type": "input",
            "block_id": "${BlockId.HOPE_DUE_DATE.name.toLowerCase()}",
            "label": ${createPlainText("희망 완료일")},
			"element": {
				"type": "datepicker",
                "action_id": "action",
				"placeholder": ${createPlainText(" ")}
			},
            "hint": ${createPlainText("완료를 희망하는 날짜")},
            "optional": false
        } 
    """

    private fun createRequestTypeSelectBlock(): String {
        return createSelectBlock(
            blockId = BlockId.REQUEST_TYPE.name.toLowerCase(),
            label = createPlainText("요청 유형"),
            placeholder = createPlainText(" "),
            options = createRequestTypeOptions(),
            hint = createPlainText("http://wiki.dailyhou.se/pages/viewpage.action?pageId=45891816 에서 표 참고"),
            optional = false
        )
    }

    private fun createRequestTypeOptions(): String {
        return RequestType.values().toList()
            .map { createOption(it.name, it.name) }
            .toString()
    }

    private fun createRequestSubjectInputBlock(): String {
        return createInputBlock(
            blockId = BlockId.REQUEST_SUBJECT.name.toLowerCase(),
            label = createPlainText("요청 제목"),
            placeholder = createPlainText(" "),
            hint = createPlainText(" "),
            optional = false
        )
    }

    private fun createRequestDescriptionInputBlock(): String {
        return createInputBlock(
            blockId = BlockId.REQUEST_DESCRIPTION.name.toLowerCase(),
            label = createPlainText("요청 상세"),
            placeholder = createPlainText(" "),
            hint = createPlainText("요청의 배경/목적과 함께 요청 내용 가급적 자세히 기재 ( ex. 유저 아이디, 매출과 같이 원하는 항목을 명시 하고 비율인 경우 분자 분모 명시 )"),
            optional = false,
            multiline = true
        )
    }

    private fun createAggregationPeriodStartDatePickerBlock() = """
        {
            "type": "input",
            "block_id": "${BlockId.AGGREGATION_PERIOD_START.name.toLowerCase()}",
            "label": ${createPlainText("집계 기간(시작)")},
			"element": {
				"type": "datepicker",
                "action_id": "action",
				"placeholder": ${createPlainText(" ")}
			},
            "hint": ${createPlainText("요청 데이터의 추출 대상 기간 from ~ to ")},
            "optional": false
        } 
    """

    private fun createAggregationPeriodEndDatePickerBlock() = """
        {
            "type": "input",
            "block_id": "${BlockId.AGGREGATION_PERIOD_END.name.toLowerCase()}",
            "label": ${createPlainText("집계 기간(끝)")},
			"element": {
				"type": "datepicker",
                "action_id": "action",
				"placeholder": ${createPlainText(" ")}
			},
            "optional": false
        } 
    """

    private fun createAggregationPeriodUnitSelectBlock(): String {
        return createSelectBlock(
            blockId = BlockId.AGGREGATION_PERIOD_UNIT.name.toLowerCase(),
            label = createPlainText("집계 기간 단위"),
            placeholder = createPlainText(" "),
            options = createAggregationPeriodUnitOptions(),
            optional = false
        )
    }

    private fun createAggregationPeriodUnitOptions(): String {
        return AggregationPeriodUnit.values().toList()
            .map { createOption(it.name, it.name) }
            .toString()
    }

    private fun createAggregationPlatformMultiSelectBlock(): String {
        return createSelectBlock(
            blockId = BlockId.AGGREGATION_PLATFORM.name.toLowerCase(),
            label = createPlainText("집계 플랫폼"),
            placeholder = createPlainText(" "),
            multiSelect = true,
            options = createAggregationPlatformOptions(),
            hint = createPlainText("리스트 중 전체 or 일부 선택"),
            optional = false
        )
    }

    private fun createAggregationPlatformOptions(): String {
        return AggregationPlatform.values().toList()
            .map { createOption(it.displayName, it.displayName) }
            .toString()
    }
}