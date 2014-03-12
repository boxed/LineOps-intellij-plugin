package net.kodare.lineops;

import com.intellij.openapi.actionSystem.AnActionEvent;


public class InvertBookmarks extends LineOpsAction {
  public void actionPerformed(AnActionEvent e) {
    this.invertBookmarks(e);
  }
}
