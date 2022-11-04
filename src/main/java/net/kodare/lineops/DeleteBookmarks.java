package net.kodare.lineops;

import com.intellij.openapi.actionSystem.AnActionEvent;


public class DeleteBookmarks extends LineOpsAction {
  public void actionPerformed(AnActionEvent e) {
    this.deleteBookmarkedLines(e, "Delete Bookmarked Lines");
  }
}
