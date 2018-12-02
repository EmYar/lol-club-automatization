package com.company

import com.company.tasks.Task
import com.company.tasks.TaskCommand
import com.company.tasks.TaskResult
import org.slf4j.LoggerFactory
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J

object Main {

    private val LOG = LoggerFactory.getLogger(Main::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            SysOutOverSLF4J.sendSystemOutAndErrToSLF4J()

            LOG.info(args.mapNotNull { TaskCommand.fromCommand(it)?.task }
                    .flatMap(Task::run)
                    .map { TaskResult::toString }
                    .joinToString { "\n" })
        } finally {
            ApiFabric.close()
        }
    }
}
