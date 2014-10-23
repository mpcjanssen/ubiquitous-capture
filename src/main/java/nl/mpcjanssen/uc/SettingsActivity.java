package nl.mpcjanssen.uc;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.io.File;

public class SettingsActivity extends Activity {
    private static UCApplication m_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_app = (UCApplication) getApplication();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        private Preference button;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onActivityCreated (Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            button = findPreference("button");
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    FileDialog dirDialog = new FileDialog(getActivity(), Environment.getExternalStorageDirectory());
                    dirDialog.setSelectDirectoryOption(true);
                    dirDialog.addDirectoryListener(new FileDialog.DirectorySelectedListener() {
                        @Override
                        public void directorySelected(File directory) {
                            m_app.setCaptureFolder(directory);
                            updateCapturePath(button);

                        }
                    });
                    dirDialog.showDialog();
                    return true;
                }
            });
            updateCapturePath(button);

        }

        private void updateCapturePath(Preference button) {
            File currentCaptureFolder = m_app.getCaptureFolder();
            button.setSummary(currentCaptureFolder.getPath());
        }
    }
}
