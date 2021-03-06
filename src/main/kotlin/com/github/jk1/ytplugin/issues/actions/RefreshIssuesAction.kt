package com.github.jk1.ytplugin.issues.actions

import com.github.jk1.ytplugin.ComponentAware
import com.github.jk1.ytplugin.logger
import com.github.jk1.ytplugin.tasks.YouTrackServer
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * Starts async issue store update from a remote server
 */
class RefreshIssuesAction(val repo: YouTrackServer) : IssueAction() {

    override val text = "Refresh issues"
    override val description = "Update issue list from YouTrack server"
    override val icon = AllIcons.Actions.Refresh!!
    override val shortcut = "control shift U"

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        if (project != null && project.isInitialized) {
            logger.debug("Issue store refresh requested for ${repo.url}")
            ComponentAware.of(project).issueStoreComponent[repo].update()
        }
    }

    override fun update(event: AnActionEvent) {
        val project = event.project
        event.presentation.isEnabled = project != null &&
                project.isInitialized &&
                !ComponentAware.of(project).issueStoreComponent[repo].isUpdating()
    }
}