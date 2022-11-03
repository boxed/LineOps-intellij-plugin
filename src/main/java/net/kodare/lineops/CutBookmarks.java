package net.kodare.lineops;

import com.intellij.openapi.actionSystem.AnActionEvent;


public class CutBookmarks extends LineOpsAction {
  public void actionPerformed(AnActionEvent e) {
    this.copyBookmarkedLines(e);
    this.deleteBookmarkedLines(e, "Cut Bookmarked Lines");
  }
}
