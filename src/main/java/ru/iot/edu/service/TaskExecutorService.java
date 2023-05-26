package ru.iot.edu.service;

import com.jcabi.ssh.Shell;
import com.jcabi.ssh.SshByPassword;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.iot.edu.config.RaspberryPiConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@Service
public class TaskExecutorService {
    @Value("${arduino-fqbn}")
    private String FQBN;
    private static final String LINUX_PATH_SEPARATOR = "/";

    private final RaspberryPiConfig raspberryPiConfig;
    private final Shell shell;

    public TaskExecutorService(RaspberryPiConfig raspberryPiConfig) throws UnknownHostException {
        this.raspberryPiConfig = raspberryPiConfig;
        this.shell = new SshByPassword(raspberryPiConfig.getAdr(), 22, raspberryPiConfig.getUser(), raspberryPiConfig.getPass());
    }

    public String sshExecute(String command) throws IOException {
        return new Shell.Plain(shell).exec(command);
    }

    public String sshExecuteWithStringError(String command) {
        try {
            return new Shell.Plain(shell).exec(command);
        } catch (IOException e) {
            return "error when run script";
        }
    }

    public String ping() {
        return sshExecuteWithStringError("echo 'pong'");
    }

    public String executeUploadedScript(String nameScript, String numberUsbPort) throws IOException {
        sshExecute(String.format(
                        "mkdir -p %s",
                        joinPathLinux(
                                raspberryPiConfig.getBaseScriptDir(),
                                nameScript
                        )
                )
        );


        String source = Path.of(getUploadedDirPath(), nameScript + ".ino").toAbsolutePath().toString();
        String destination = joinPathLinux(

                raspberryPiConfig.getBaseScriptDir(),
                nameScript,
                nameScript + ".ino"
        );
        Scp scp = new Scp();
        scp.setLocalFile(source);
        scp.setTodir(raspberryPiConfig.getUser() + ":" + raspberryPiConfig.getPass() + "@" + raspberryPiConfig.getAdr() + ":" + destination);
        scp.setProject(new Project());
        scp.setTrust(true);
        scp.execute();

        String compileResult = sshExecute(
                String.format(
                        "arduino-cli compile -b  %s %s",
                        FQBN,
                        joinPathLinux(raspberryPiConfig.getScriptsFolder(), nameScript)
                )
        );
        String flushResult = sshExecute(
                String.format(
                        "arduino-cli upload -p /dev/tty%s %s %s %s",
                        numberUsbPort,
                        "--fqbn",
                        FQBN,
                        joinPathLinux(raspberryPiConfig.getScriptsFolder(), nameScript)
                )
        );
        return compileResult + flushResult;

    }

    private String getUploadedDirPath() {
        Path root = FileSystems.getDefault().getPath("").toAbsolutePath();
        return Path.of(root.toString(), "uploaded").toAbsolutePath().toString();
    }

    private static File fileWithDirectoryAssurance(String directory, String filename) {
        File dir = new File(directory);
        if (!dir.exists()) {
            boolean isCreatedDir = dir.mkdirs();
            if (!isCreatedDir) throw new IllegalArgumentException("error when eh folder create");
        }
        return new File(Path.of(directory, filename).toUri());
    }


    public String uploadScript(String name, MultipartFile file) {
        String pathToUploaded = fileWithDirectoryAssurance(
                getUploadedDirPath(),
                name + ".ino"
        ).getAbsolutePath();
        if (!file.isEmpty()) {

            try {
                byte[] bytes = file.getBytes();
                try (BufferedOutputStream stream =
                             new BufferedOutputStream(new FileOutputStream(pathToUploaded))
                ) {
                    stream.write(bytes);
                }
                return "Вы удачно загрузили " + name + " в " + name + "-uploaded !";
            } catch (Exception e) {
                return "Вам не удалось загрузить " + name + " => " + e.getMessage();
            }
        } else {
            return "Вам не удалось загрузить " + name + " потому что файл пустой.";
        }
    }

    private String joinPathLinux(String... args) {
        return String.join(
                LINUX_PATH_SEPARATOR,
                args
        );
    }

}
