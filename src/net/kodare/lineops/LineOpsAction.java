package net.kodare.lineops;

import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.ide.bookmarks.Bookmark;
import com.intellij.ide.bookmarks.BookmarkManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;

import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public abstract class LineOpsAction extends AnAction {
  public void copyBookmarkedLines(AnActionEvent e) {
    Editor editor = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE);
    if (editor == null) {
      return;
    }

    ArrayList<String> lines = new ArrayList<String>();
    for (Bookmark b : BookmarkManager.getInstance(e.getData(CommonDataKeys.PROJECT)).getValidBookmarks()) {
      Document doc = b.getDocument();
      if (doc == editor.getDocument()) {
        lines.add(doc.getText(new TextRange(doc.getLineStartOffset(b.getLine()),
                                            doc.getLineEndOffset(b.getLine()))));
      }
    }

    CopyPasteManagerEx.getInstanceEx().setContents(new StringSelection(StringUtil.join(lines, "\n")));
  }

  public void deleteBookmarkedLines(AnActionEvent e, String title) {
    final Editor editor = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE);
    if (editor == null) {
      return;
    }

    BookmarkManager bookmarkManager = BookmarkManager.getInstance(e.getData(CommonDataKeys.PROJECT));
    final ArrayList<Integer> lines = new ArrayList<Integer>();
    for (Bookmark b : bookmarkManager.getValidBookmarks()) {
      Document doc = b.getDocument();
      if (doc == editor.getDocument()) {
        lines.add(b.getLine());
      }
    }
    Collections.sort(lines, Collections.reverseOrder());

    Runnable runnable = new Runnable() {
      public void run() {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
          public void run() {
            Document doc = editor.getDocument();
            for (int i : lines) {
              int endOffset = doc.getLineEndOffset(i);
              if (i != doc.getLineCount() - 1) {
                endOffset++;
              }
              doc.deleteString(doc.getLineStartOffset(i), endOffset);
            }
          }
        });
      }
    };

    CommandProcessor.getInstance().executeCommand(e.getData(CommonDataKeys.PROJECT),
                                                  runnable,
                                                  title,
                                                  null);
  }

  public void clearBookmarks(AnActionEvent e) {
    Editor editor = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE);
    if (editor == null) {
      return;
    }

    BookmarkManager bookmarkManager = BookmarkManager.getInstance(e.getData(CommonDataKeys.PROJECT));
    for (Bookmark b : bookmarkManager.getValidBookmarks()) {
      Document doc = b.getDocument();
      if (doc == editor.getDocument()) {
        bookmarkManager.removeBookmark(b);
      }
    }
  }

  public void invertBookmarks(AnActionEvent e) {
    Editor editor = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE);
    if (editor == null) {
      return;
    }

    BookmarkManager bookmarkManager = BookmarkManager.getInstance(e.getData(CommonDataKeys.PROJECT));
    HashMap<Integer, Bookmark> bookmarkFromLines = new HashMap<Integer, Bookmark>();
    for (Bookmark b : bookmarkManager.getValidBookmarks()) {
      Document doc = b.getDocument();
      if (doc == editor.getDocument()) {
        bookmarkFromLines.put(b.getLine(), b);
      }
    }

    for (int i = 0; i != editor.getDocument().getLineCount(); i++) {
      if (bookmarkFromLines.containsKey(i)) {
        bookmarkManager.removeBookmark(bookmarkFromLines.get(i));
      }
      else {
        bookmarkManager.addEditorBookmark(editor, i);
      }
    }
  }
}
