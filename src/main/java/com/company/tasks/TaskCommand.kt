package com.company.tasks

import one.util.streamex.StreamEx

enum class TaskCommand(val command: String,
                       val task: Task) {

    UPDATE_NAMES("updateNames", NamesUpdater());

    companion object {

        fun fromCommand(command: String): TaskCommand? {
            return StreamEx.of(*values())
                    .findFirst { task -> task.command == command }
                    .orElse(null)
        }
    }
}
