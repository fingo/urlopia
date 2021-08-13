package info.fingo.urlopia.api.v2.info;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/api/v2/info")
@RequiredArgsConstructor
public class InfoControllerV2 {

    private final InfoService infoService;


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public InfoOutput getApplicationInfo() {
        var applicationInfoDto = infoService.getApplicationInfo();
        return InfoOutput.fromApplicationInfoDTO(applicationInfoDto);
    }
}
