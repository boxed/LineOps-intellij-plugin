package net.kodare.lineops;

import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.ide.bookmark.*;
import com.intellij.ide.bookmark.providers.LineBookmarkProvider;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;

import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public abstract class LineOpsAction extends AnAction {
  private static boolean isBookmarkFromCurrentVirtualFile(AnActionEvent e, FileBookmark fileBookmark) {
    return fileBookmark.getFile().equals(e.getData(CommonDataKeys.VIRTUAL_FILE));
  }

  public void copyBookmarkedLines(AnActionEvent e) {
    final var editor = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE);
    if (editor == null) {
      return;
    }

    final var bookmarksManager = BookmarksManager.getInstance(e.getData(CommonDataKeys.PROJECT));
    if (bookmarksManager == null) {
      return;
    }
    final ArrayList<String> lines = new ArrayList<>();
    for (Bookmark b : bookmarksManager.getBookmarks()) {
      if (b instanceof LineBookmark lineBookmark && isBookmarkFromCurrentVirtualFile(e, lineBookmark)) {
        final Document doc = editor.getDocument();
        final int line = lineBookmark.getLine();
        lines.add(doc.getText(new TextRange(doc.getLineStartOffset(line), doc.getLineEndOffset(line))));
      }
    }

    CopyPasteManagerEx.getInstanceEx().setContents(new StringSelection(StringUtil.join(lines, "\n")));
  }

  public void deleteBookmarkedLines(AnActionEvent e, String title) {
    final var editor = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE);
    if (editor == null) {
      return;
    }

    final var bookmarksManager = BookmarksManager.getInstance(e.getData(CommonDataKeys.PROJECT));
    if (bookmarksManager == null) {
      return;
    }
    final ArrayList<Integer> lines = new ArrayList<>();
    for (Bookmark b : bookmarksManager.getBookmarks()) {
      if (b instanceof LineBookmark lineBookmark && isBookmarkFromCurrentVirtualFile(e, lineBookmark)) {
        lines.add(lineBookmark.getLine());
      }
    }
    lines.sort(Collections.reverseOrder());

    this.clearBookmarks(e);

    Runnable runnable = () -> ApplicationManager.getApplication().runWriteAction(() -> {
      final Document doc = editor.getDocument();
      for (int i : lines) {
        int endOffset = doc.getLineEndOffset(i);
        if (i != doc.getLineCount() - 1) {
          endOffset++;
        }
        doc.deleteString(doc.getLineStartOffset(i), endOffset);
      }
    });

    CommandProcessor.getInstance().executeCommand(e.getData(CommonDataKeys.PROJECT),
                                                  runnable,
                                                  title,
                                                  null);
  }

  public void clearBookmarks(AnActionEvent e) {
    final var editor = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE);
    if (editor == null) {
      return;
    }

    final var bookmarksManager = BookmarksManager.getInstance(e.getData(CommonDataKeys.PROJECT));
    if (bookmarksManager == null) {
      return;
    }
    for (Bookmark b : bookmarksManager.getBookmarks()) {
      if (b instanceof FileBookmark fileBookmark) {
        if (isBookmarkFromCurrentVirtualFile(e, fileBookmark)) {
          bookmarksManager.remove(fileBookmark);
        }
      }
    }
  }

  public void invertBookmarks(AnActionEvent e) {
    final var editor = e.getData(CommonDataKeys.EDITOR_EVEN_IF_INACTIVE);
    if (editor == null) {
      return;
    }

    final var project = e.getData(CommonDataKeys.PROJECT);
    final var bookmarksManager =  BookmarksManager.getInstance(project);
    if (bookmarksManager == null) {
      return;
    }
    final var lineBookmarkProvider = LineBookmarkProvider.find(project);
    if (lineBookmarkProvider == null) {
      return;
    }
    final var virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
    if (virtualFile == null) {
      return;
    }

    final HashMap<Integer, Bookmark> bookmarkFromLines = new HashMap<>();
    for (Bookmark b : bookmarksManager.getBookmarks()) {
      if (b instanceof LineBookmark lineBookmark && isBookmarkFromCurrentVirtualFile(e, lineBookmark)) {
        bookmarkFromLines.put(lineBookmark.getLine(), lineBookmark);
      }
    }

    for (int i = 0; i != editor.getDocument().getLineCount(); i++) {
      if (bookmarkFromLines.containsKey(i)) {
        bookmarksManager.remove(bookmarkFromLines.get(i));
      }
      else {
        final FileBookmark bookmark = lineBookmarkProvider.createBookmark(virtualFile, i);
        if (bookmark == null) {
          continue;
        }
        bookmarksManager.add(bookmark, BookmarkType.DEFAULT);
      }
    }
  }
}
