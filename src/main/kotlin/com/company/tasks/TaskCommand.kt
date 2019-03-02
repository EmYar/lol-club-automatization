package com.company.tasks

enum class TaskCommand(val command: String,
                       val task: Task) {

    UPDATE_NAMES("updateNames", NamesUpdater()),
    SAVE_SCORES("saveScores", ScoreSaver());

    companion object {
        fun fromCommand(command: String): TaskCommand? {
            return values().find { it.command == command }
        }
    }
}
