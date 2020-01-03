package routes.interactions.requests

data class InteractionRequestBody(
    val user: User,
    val triggerId: String,
    val view: View
) {
    data class User(
        val id: String
    )

    data class View(
        val callbackId: String,
        val state: State
    ) {
        data class State(
            val values: Values
        ) {
            data class Values(
                val hopeResponsiblePerson: SelectValue,
                val hopeDueDate: DatePickerValue,
                val requestType: SelectValue,
                val requestSubject: TextValue,
                val requestDescription: TextValue,
                val aggregationPeriodStart: DatePickerValue,
                val aggregationPeriodEnd: DatePickerValue,
                val aggregationPeriodUnit: SelectValue,
                val aggregationPlatform: MultiSelectValue
            ) {
                data class DatePickerValue(
                    val action: DatePickerAction
                ) {
                    data class DatePickerAction(
                        val selectedDate: String?
                    )
                }

                data class TextValue(
                    val action: TextAction
                ) {
                    data class TextAction(
                        val value: String?
                    )
                }

                data class SelectValue(
                    val action: SelectAction
                ) {
                    data class SelectAction(
                        val selectedOption: SelectedOption?
                    ) {
                        data class SelectedOption(
                            val value: String
                        )
                    }
                }

                data class MultiSelectValue(
                    val action: SelectAction
                ) {
                    data class SelectAction(
                        val selectedOptions: List<SelectedOption>?
                    ) {
                        data class SelectedOption(
                            val value: String
                        )
                    }
                }
            }
        }
    }
}