package ru.iot.edu.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.iot.edu.service.TaskExecutorService;

import java.io.IOException;


@RestController()
@RequestMapping("api/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    private final TaskExecutorService taskExecutorService;

    public TestController(TaskExecutorService taskExecutorService) {
        this.taskExecutorService = taskExecutorService;
    }


    @GetMapping("/ping_stand")
    public String ping() {
        return taskExecutorService.ping();
    }


    @GetMapping("/execute_script")
    public String executeUploadedScript(
            @RequestParam("name") String name,
            @RequestParam("numberUsbPort") String numberUsbPort
    ) throws IOException {
        return taskExecutorService.executeUploadedScript(name, numberUsbPort);
    }

    @PostMapping(value = "/upload_script")
    public @ResponseBody String uploadScript(@RequestParam("name") String name,
                                             @RequestParam("file") MultipartFile file) {
        return taskExecutorService.uploadScript(name, file);
    }

}
