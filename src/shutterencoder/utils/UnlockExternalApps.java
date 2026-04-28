package shutterencoder.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.json.JSONObject;

public class UnlockExternalApps {

	public static String checkBrowser() throws Exception {
        // Registry key for default HTTP handler
        String key = "HKCU\\Software\\Microsoft\\Windows\\Shell\\Associations\\UrlAssociations\\http\\UserChoice";

        Process process = new ProcessBuilder("reg", "query", key, "/v", "ProgId")
                .redirectErrorStream(true)
                .start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            String progId = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ProgId")) {
                    // Example output:    ProgId    REG_SZ    ChromeHTML
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length >= 3) {
                        progId = parts[parts.length - 1].trim();
                    }
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0 && progId != null)
            {
                return resolveBrowser(progId);
            }
        }
        
		return null;
    }

    private static String resolveBrowser(String progId) {
        switch (progId) {
            case "ChromeHTML": return "Google Chrome";
            case "MSEdgeHTM":
            case "MSEdgeHTMHTML": return "Microsoft Edge";
            case "FirefoxURL": return "Mozilla Firefox";
            case "IE.HTTP": return "Internet Explorer";
            default: return "Unknown (" + progId + ")";
        }
    }
    
    public static String checkDownloadLocation() throws Exception {
    	String defaultBrowser = getDefaultBrowserProgId();

        String downloadFolder = switch (defaultBrowser) {
            case "ChromeHTML" -> getChromeOrEdgeDownloadFolder(
                    Paths.get(System.getenv("LOCALAPPDATA"), "Google", "Chrome", "User Data", "Default", "Preferences"));
            case "MSEdgeHTM", "MSEdgeHTMHTML" -> getChromeOrEdgeDownloadFolder(
                    Paths.get(System.getenv("LOCALAPPDATA"), "Microsoft", "Edge", "User Data", "Default", "Preferences"));
            case "FirefoxURL" -> getFirefoxDownloadFolder();
            case "IE.HTTP" -> getIEDownloadFolder();
            default -> System.getProperty("user.home") + "\\Downloads";
        };

        return downloadFolder;
    }

    private static String getDefaultBrowserProgId() throws Exception {
        Process p = new ProcessBuilder("reg", "query",
                "HKCU\\Software\\Microsoft\\Windows\\Shell\\Associations\\UrlAssociations\\http\\UserChoice",
                "/v", "ProgId").start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("ProgId")) {
                String[] parts = line.split("\\s{2,}");
                if (parts.length >= 3) return parts[2];
            }
        }
        p.waitFor();
        return "";
    }

    private static String getChromeOrEdgeDownloadFolder(Path prefsPath) {
        String fallback = System.getProperty("user.home") + "\\Downloads";
        try {
            if (!Files.exists(prefsPath)) return fallback;
            String content = Files.readString(prefsPath, StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(content);
            JSONObject download = json.optJSONObject("download");
            if (download != null) {
                String dir = download.optString("default_directory", "").trim();
                if (!dir.isEmpty()) return dir;
            }
        } catch (Exception ignored) { }
        return fallback;
    }

    private static String getFirefoxDownloadFolder() {
        String fallback = System.getProperty("user.home") + "\\Downloads";
        try {
            Path appData = Paths.get(System.getenv("APPDATA"), "Mozilla", "Firefox", "Profiles");
            Optional<Path> profile = Files.list(appData).filter(Files::isDirectory).findFirst();
            if (profile.isEmpty()) return fallback;
            Path prefsJs = profile.get().resolve("prefs.js");
            if (!Files.exists(prefsJs)) return fallback;
            for (String line : Files.readAllLines(prefsJs, StandardCharsets.UTF_8)) {
                line = line.trim();
                if (line.startsWith("user_pref(\"browser.download.dir\"")) {
                    int firstQuote = line.indexOf('"', 27);
                    int secondQuote = line.indexOf('"', firstQuote + 1);
                    if (firstQuote > 0 && secondQuote > firstQuote) {
                        return line.substring(firstQuote + 1, secondQuote);
                    }
                }
            }
        } catch (Exception ignored) { }
        return fallback;
    }

    private static String getIEDownloadFolder() {
        String fallback = System.getProperty("user.home") + "\\Downloads";
        try {
            Process p = new ProcessBuilder("reg", "query",
                    "HKCU\\Software\\Microsoft\\Internet Explorer\\Main",
                    "/v", "Download Directory").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Download Directory")) {
                    String[] parts = line.split("\\s{2,}");
                    if (parts.length >= 3) return parts[2];
                }
            }
            p.waitFor();
        } catch (Exception ignored) { }
        return fallback;
    }    
    
    public static File waitForFile(Path desktop, Path downloads, String keyword) throws InterruptedException {
      
    	while (true) {
    		
            // Check Desktop
            File f = findMatch(desktop, keyword);
            if (f != null) return f;

            // Check Downloads
            f = findMatch(downloads, keyword);
            if (f != null)
            	return f;

            Thread.sleep(1000);
        }
    }

	private static File findMatch(Path folder, String keyword) {
        File dir = folder.toFile();
        if (dir.exists() && dir.isDirectory()) {
            File[] matches = dir.listFiles((d, name) -> 
                name.toLowerCase().contains(keyword.toLowerCase()) &&
                name.toLowerCase().endsWith(".exe")
            );
            if (matches != null && matches.length > 0) {
                return matches[0];
            }
        }
        return null;
    }
}
