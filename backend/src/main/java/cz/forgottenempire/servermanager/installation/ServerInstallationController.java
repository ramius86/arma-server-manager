package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.common.ServerType;

import java.util.List;

import cz.forgottenempire.servermanager.serverinstance.dtos.AutomaticRestartDto;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/server/installation")
@Slf4j
class ServerInstallationController {

    private final ServerInstallationService installationService;
    private final ServerInstallerService installerService;
    private final ServerInstallationMapper mapper = Mappers.getMapper(ServerInstallationMapper.class);

    @Autowired
    public ServerInstallationController(
            ServerInstallationService installationService,
            ServerInstallerService installerService
    ) {
        this.installationService = installationService;
        this.installerService = installerService;
    }

    @GetMapping
    @Cacheable("serverInstallationsResponse")
    public ResponseEntity<ServerInstallationsDto> getAllInstalations() {
        log.debug("Getting server installations");
        List<ServerInstallation> installations = installationService.getAvailableServerInstallations();
        return ResponseEntity.ok(new ServerInstallationsDto(mapper.map(installations)));
    }

    @GetMapping("/{type}")
    public ResponseEntity<ServerInstallationDto> getInstallation(@PathVariable ServerType type) {
        ServerInstallation installation = installationService.getServerInstallation(type);
        return ResponseEntity.ok(mapper.map(installation));
    }

    @PostMapping("/{type}")
    public ResponseEntity<ServerInstallationDto> installOrUpdateServer(@PathVariable ServerType type) {
        ServerInstallation serverInstallation = installationService.getServerInstallation(type);
        installerService.installServer(serverInstallation);
        return ResponseEntity.ok(mapper.map(serverInstallation));
    }

    @PatchMapping("/{type}")
    public ResponseEntity<?> setServerBranch(@PathVariable ServerType type, @RequestBody ActiveBranchDto activeBranchDto) {
        ServerInstallation installation = installationService.getServerInstallation(type);
        installationService.setServerBranch(installation, activeBranchDto.branch());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
