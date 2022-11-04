package net.kodare.lineops;

import com.intellij.openapi.actionSystem.AnActionEvent;


public class ClearBookmarks extends LineOpsAction {
  public void actionPerformed(AnActionEvent e) {
    this.clearBookmarks(e);
  }
}
