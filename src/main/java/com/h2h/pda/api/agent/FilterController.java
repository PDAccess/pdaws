package com.h2h.pda.api.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.h2h.pda.entity.ExecFileFilter;
import com.h2h.pda.entity.ExecTraceFilter;
import com.h2h.pda.pojo.CommandFilter;
import com.h2h.pda.pojo.FileFilter;
import com.h2h.pda.repository.ExecFileFilterRepository;
import com.h2h.pda.repository.ExecTraceFilterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/filter")
public class FilterController {
    private final Logger logger = LoggerFactory.getLogger(FilterController.class);

    @Autowired
    ExecTraceFilterRepository execTraceFilterRepository;
    @Autowired
    ExecFileFilterRepository execFileFilterRepository;


    @GetMapping("/file/all/{serviceId}/{groupId}")
    public ResponseEntity<FileFilter> getFileFilters(@PathVariable String serviceId,
                                                     @PathVariable String groupId) {

        List<ExecFileFilter> execFileFilters = execFileFilterRepository.findAllByServiceIdAndGroupId(serviceId, groupId);
        List<String> users = new ArrayList<>();
        List<String> paths = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (ExecFileFilter fileFilter : execFileFilters) {
            try {
                String[] usersArray = mapper.readValue(fileFilter.getUsers(), String[].class);
                String[] pathArray = mapper.readValue(fileFilter.getPaths(), String[].class);
                users.addAll(Arrays.asList(usersArray));
                paths.addAll(Arrays.asList(pathArray));
            } catch (JsonProcessingException e) {
                logger.warn("There is a problem while parsing json {}", e.getMessage());
            }
        }

        FileFilter fileFilter = new FileFilter(users, paths);

        return new ResponseEntity<>(fileFilter, HttpStatus.OK);
    }

    @GetMapping("/command/all/{serviceId}/{groupId}")
    public ResponseEntity<CommandFilter> getCommandFilters(@PathVariable String serviceId,
                                                           @PathVariable String groupId) {

        List<ExecTraceFilter> execTraceFilters = execTraceFilterRepository.findAllByServiceIdAndGroupId(serviceId, groupId);

        List<String> users = new ArrayList<>();
        List<String> regex = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (ExecTraceFilter commandFilter : execTraceFilters) {
            try {
                String[] usersArray = mapper.readValue(commandFilter.getUsers(), String[].class);
                String[] regexArray = mapper.readValue(commandFilter.getRegexes(), String[].class);
                users.addAll(Arrays.asList(usersArray));
                regex.addAll(Arrays.asList(regexArray));
            } catch (JsonProcessingException e) {
                logger.warn("There is a problem while parsing json {}", e.getMessage());
            }
        }

        CommandFilter commandFilter = new CommandFilter(users, regex);

        return new ResponseEntity<>(commandFilter, HttpStatus.OK);
    }

    @GetMapping("/file/service/{serviceId}")
    public ResponseEntity<List<ExecFileFilter>> getFileServiceFilter(@PathVariable String serviceId) {

        List<ExecFileFilter> execFileFilters = execFileFilterRepository.findAllByServiceId(serviceId);

        return new ResponseEntity<>(execFileFilters, HttpStatus.OK);
    }

    @GetMapping("/file/group/{groupId}")
    public ResponseEntity<List<ExecFileFilter>> getFileGroupFilter(@PathVariable String groupId) {

        List<ExecFileFilter> execFileFilters = execFileFilterRepository.findAllByGroupId(groupId);

        return new ResponseEntity<>(execFileFilters, HttpStatus.OK);
    }

    @GetMapping("/command/service/{serviceId}")
    public ResponseEntity<List<ExecTraceFilter>> getCommandServiceFilter(@PathVariable String serviceId) {

        List<ExecTraceFilter> execTraceFilters = execTraceFilterRepository.findAllByServiceId(serviceId);

        return new ResponseEntity<>(execTraceFilters, HttpStatus.OK);
    }

    @GetMapping("/command/group/{groupId}")
    public ResponseEntity<List<ExecTraceFilter>> getCommandGroupFilter(@PathVariable String groupId) {

        List<ExecTraceFilter> execTraceFilters = execTraceFilterRepository.findAllByGroupId(groupId);

        return new ResponseEntity<>(execTraceFilters, HttpStatus.OK);
    }

    @PostMapping("/file")
    public ResponseEntity<Void> createFileFilter(@RequestParam(value = "name") String name,
                                                 @RequestParam(value = "description") String description,
                                                 @RequestParam(value = "users") String users,
                                                 @RequestParam(value = "paths") String paths,
                                                 @RequestParam(value = "service_id", required = false) String serviceId,
                                                 @RequestParam(value = "group_id", required = false) String groupId) {

        ExecFileFilter fileFilter = new ExecFileFilter();
        fileFilter.setName(name);
        fileFilter.setDescription(description);
        fileFilter.setServiceId(serviceId);
        fileFilter.setGroupId(groupId);
        fileFilter.setUsers(users);
        fileFilter.setPaths(paths);
        execFileFilterRepository.save(fileFilter);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/command")
    public ResponseEntity<Void> createCommandFilter(@RequestParam(value = "name") String name,
                                                    @RequestParam(value = "description") String description,
                                                    @RequestParam(value = "users") String users,
                                                    @RequestParam(value = "regexes") String regexes,
                                                    @RequestParam(value = "service_id", required = false) String serviceId,
                                                    @RequestParam(value = "group_id", required = false) String groupId) {

        ExecTraceFilter execTraceFilter = new ExecTraceFilter();
        execTraceFilter.setName(name);
        execTraceFilter.setDescription(description);
        execTraceFilter.setUsers(users);
        execTraceFilter.setRegexes(regexes);
        execTraceFilter.setServiceId(serviceId);
        execTraceFilter.setGroupId(groupId);
        execTraceFilterRepository.save(execTraceFilter);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/file/{filterId}")
    public ResponseEntity<ExecFileFilter> getFileFilter(@PathVariable Integer filterId) {
        Optional<ExecFileFilter> optional = execFileFilterRepository.findById(filterId);
        return optional.map(execFileFilter -> new ResponseEntity<>(execFileFilter, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping("/command/{filterId}")
    public ResponseEntity<ExecTraceFilter> getCommandFilter(@PathVariable Integer filterId) {
        Optional<ExecTraceFilter> optional = execTraceFilterRepository.findById(filterId);
        return optional.map(execTraceFilter -> new ResponseEntity<>(execTraceFilter, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @PutMapping("/file/{filterId}")
    public ResponseEntity<Void> editFileFilter(@RequestParam(value = "name") String name,
                                               @RequestParam(value = "description") String description,
                                               @RequestParam(value = "users") String users,
                                               @RequestParam(value = "paths") String paths,
                                               @PathVariable Integer filterId) {

        Optional<ExecFileFilter> optional = execFileFilterRepository.findById(filterId);
        if (!optional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        ExecFileFilter execFileFilter = optional.get();
        execFileFilter.setName(name);
        execFileFilter.setDescription(description);
        execFileFilter.setPaths(paths);
        execFileFilter.setUsers(users);
        execFileFilterRepository.save(execFileFilter);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/command/{filterId}")
    public ResponseEntity<Void> editCommandFilter(@RequestParam(value = "name") String name,
                                                  @RequestParam(value = "description") String description,
                                                  @RequestParam(value = "users") String users,
                                                  @RequestParam(value = "regexes") String regexes,
                                                  @PathVariable Integer filterId) {

        Optional<ExecTraceFilter> optional = execTraceFilterRepository.findById(filterId);
        if (!optional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        ExecTraceFilter execTraceFilter = optional.get();
        execTraceFilter.setName(name);
        execTraceFilter.setDescription(description);
        execTraceFilter.setRegexes(regexes);
        execTraceFilter.setUsers(users);
        execTraceFilterRepository.save(execTraceFilter);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/file/{filterId}")
    public ResponseEntity<Void> deleteFileFilter(@PathVariable Integer filterId) {

        execFileFilterRepository.deleteById(filterId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/command/{filterId}")
    public ResponseEntity<Void> deleteCommandFilter(@PathVariable Integer filterId) {

        execTraceFilterRepository.deleteById(filterId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
