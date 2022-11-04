package net.kodare.lineops;

import com.intellij.openapi.actionSystem.AnActionEvent;


public class CopyBookmarks extends LineOpsAction {
  public void actionPerformed(AnActionEvent e) {
    this.copyBookmarkedLines(e);
  }
}
