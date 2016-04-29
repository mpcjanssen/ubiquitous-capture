package nl.mpcjanssen.uc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileDialog {
    private static final String PARENT_DIR = "..";
    private final String TAG = getClass().getName();
    private final File initialPath;
    private final Activity activity;
    private ArrayList<String> fileList = new ArrayList<>();
    private File currentPath;
    private ListenerList<FileSelectedListener> fileListenerList = new ListenerList<>();
    private ListenerList<DirectorySelectedListener> dirListenerList = new ListenerList<>();
    private boolean selectDirectoryOption;

    /**
     * @param activity The activity to diaplay the file dialog in.
     * @param initialPath The initial path.
     */
    public FileDialog(Activity activity, File initialPath) {
        this.activity = activity;
        if (initialPath.exists()) {
            this.initialPath = initialPath;
        } else {
            this.initialPath = Environment.getExternalStorageDirectory();
        }
    }

    /**
     * @return file dialog
     */
    public Dialog createFileDialog(File path) {

        if (path == null) {
            loadFileList(initialPath);
        } else {
            loadFileList(path);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(currentPath.getPath());
        if (selectDirectoryOption) {
            builder.setPositiveButton("Select directory", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, currentPath.getPath());
                    fireDirectorySelectedEvent(currentPath);
                }
            });
        }

        builder.setItems(fileList.toArray(new String[fileList.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String fileChosen = fileList.get(which);
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory()) {
                    currentPath = chosenFile;
                    dialog.cancel();
                    dialog.dismiss();
                    showDialog(currentPath);
                } else fireFileSelectedEvent(chosenFile);
            }
        });
        return builder.show();
    }

    public void setSelectDirectoryOption(boolean selectDirectoryOption) {
        this.selectDirectoryOption = selectDirectoryOption;
    }

    public void addDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.add(listener);
    }

    /**
     * Show file dialog
     */
    public void showDialog(File path) {
        createFileDialog(path).show();
    }

    private void fireFileSelectedEvent(final File file) {
        fileListenerList.fireEvent(new ListenerList.FireHandler<FileSelectedListener>() {
            public void fireEvent(FileSelectedListener listener) {
                listener.fileSelected(file);
            }
        });
    }

    private void fireDirectorySelectedEvent(final File directory) {
        dirListenerList.fireEvent(new ListenerList.FireHandler<DirectorySelectedListener>() {
            public void fireEvent(DirectorySelectedListener listener) {
                listener.directorySelected(directory);
            }
        });
    }

    private void loadFileList(File path) {
        Log.v("FileDialog", "Loading files for " + path.toString());
        this.currentPath = path;
        List<String> r = new ArrayList<>();

        if (path.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    return sel.canRead() && sel.isDirectory();
                }
            };
            String[] fileList1 = path.list(filter);
            Collections.addAll(r, fileList1);
        }
        Collections.sort(r);
        fileList.clear();
        fileList.addAll(r);
    }

    private File getChosenFile(String fileChosen) {
        Log.v("FileDialog", "File choosen: " + fileChosen);
        if (fileChosen.equals(PARENT_DIR)) {
            return currentPath.getParentFile();
        } else {
            return new File(currentPath, fileChosen);
        }
    }

    public interface FileSelectedListener {
        void fileSelected(File file);
    }

    public interface DirectorySelectedListener {
        void directorySelected(File directory);
    }
}

class ListenerList<L> {
    private List<L> listenerList = new ArrayList<>();

    public void add(L listener) {
        listenerList.add(listener);
    }

    public void fireEvent(FireHandler<L> fireHandler) {
        List<L> copy = new ArrayList<>(listenerList);
        for (L l : copy) {
            fireHandler.fireEvent(l);
        }
    }

    public interface FireHandler<L> {
        void fireEvent(L listener);
    }
}
