package com.driveutils;

import com.google.api.client.http.FileContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.google.api.services.drive.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UploadImpl{

    private static final String URL_FOLDER = "https://drive.google.com/drive/u/0/folders/";


    /*
    * UploadImpl de arquivo, criando uma pasta para incluir o arquivo
    * */

    public static String uploadAndCreateFolder(String fileName, String folderName, String path, String type){
        log.info("chamando >>>>>>>>> uploadAndCreateFolder");
        String folder = "";

        Map<String, String> folderMap = searchPath();
        //cria a pasta caso nao exista no diretorio, se existir, nao cria e atribui o nome da pasta a variavel de todo jeito
        if(!folderMap.containsKey(folderName)) {
            createFolder(folderName);
        }

        folder = folderName;

        HttpStatus httpStatus = uploadInExistentFolder(folderName, fileName, type, path);

        return httpStatus.toString();
    }

    /*
    * UploadImpl simples de arquivo
    * */
    public static void upload(String fileName, String path, String type){
       log.info("chamando ->>>>>>>>>>. upload");
        //define o nome do arquivo
        com.google.api.services.drive.model.File dataFile = new com.google.api.services.drive.model.File();
        dataFile.setName(fileName);

        //caminho do arquivo na maquina
        File filePath = new File(path);

        //tipo do arquivo
        FileContent mediaContent = new FileContent(type, filePath);
        com.google.api.services.drive.model.File file = null;
        try {
            file = Config.buildDrive().files().create(dataFile, mediaContent).setFields("id").execute();
        } catch (Exception e) {
            UtilMessageError.printMessageError(e);
        }
        log.info("File ID: " + file.getId());
    }

    /**
     * Insere um arquivo em uma pasta ja existente dentro do drive
     * */
    public static HttpStatus uploadInExistentFolder(String nameFolder, String fileName, String type, String path) {
        log.info("chamando >>>>>>>>>>>> uploadInExistentFolder");
        String folderId = "";

        try {
            final Map<String, String> stringStringMap = searchPath();

            if (stringStringMap.containsKey(nameFolder))
                folderId = stringStringMap.get(nameFolder);
            else log.error("A pasta com nome de: {} nao foi encontrada ou nao existe", nameFolder);


            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();

            fileMetadata.setName(fileName);
            fileMetadata.setParents(Collections.singletonList(folderId));

            java.io.File filePath = new java.io.File(path);
            FileContent mediaContent = new FileContent(type, filePath);

            com.google.api.services.drive.model.File file = Config.buildDrive().files()
                    .create(fileMetadata, mediaContent)
                    .setFields("id, parents").execute();

            log.info("Link da pasta: \n" + URL_FOLDER + folderId);


            if (HttpStatus.CREATED.is2xxSuccessful()) {
                log.info("Retornou: {}", HttpStatus.CREATED.toString());
                return HttpStatus.CREATED;
            } else {
                log.error("Ocorreu um erro na requisicao");
                return HttpStatus.FORBIDDEN;
            }

        } catch (IOException e) {
            log.error("Ocorreu um erro ao criar arquivo dentro da pasta");
            UtilMessageError.printMessageError(e);
        }

        return HttpStatus.BAD_REQUEST;
    }

    /*
    * Lista as pastas existentes no drive
    * */
    public static Map<String, String> searchPath() {
        String pageToken = null;
        Map<String, String> listPath = new HashMap<>();
        try {
            do {
                FileList result = Config.buildDrive().files().list().setQ("mimeType = 'application/vnd.google-apps.folder'").setSpaces("drive").setFields("nextPageToken, files(id, name)").setPageToken(pageToken).execute();
                for (com.google.api.services.drive.model.File file : result.getFiles()) {
                    log.debug("Pasta encontrada: {}", file.getName());

                    listPath.put(file.getName(), file.getId());
                }
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
        } catch (IOException e){
            log.error("NÃO FOI POSSIVEL PESQUISAR AS PASTAS");
            UtilMessageError.printMessageError(e);

        }
        return listPath;
    }

    /*
    * Cria pasta
    * */
    public static String createFolder(String name){
        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(name);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        com.google.api.services.drive.model.File file = null;

        try {
            file = Config.buildDrive().files().create(fileMetadata)
                    .setFields("id")
                    .execute();

            log.info("Link da pasta criada:\n https://drive.google.com/drive/u/0/folders/" + file.getId());
            log.info("Folder created with name: {}", name);

        return file.getId();

        } catch (Exception e) {
            log.error("Erro ao criar pasta: {}", e.getMessage());
            UtilMessageError.printMessageError(e);
            return null;
        }
    }

    public static void main(String[] args) {

        //TESTES ->>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//        createFolder(Config.buildDrive());
//        try {
//            final Map<String, String> stringStringMap = searchPathForName(Config.buildDrive(),"Classroom");
//            if (stringStringMap.containsValue("Classroom")) {
//                System.out.println(stringStringMap.get("Classroom"));
//                System.out.println("tem");
//            }
//            } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        uploadInExistentFolder(Config.buildDrive(), "Classroom", "aa.rar", TypeContents.RAR.getType());
//            createFolder(Config.buildDrive(), "nova pasta");
//        uploadImpl.uploadAndCreateFolder("RAR.RAR", "nova pasta", "aa.rar", TypeContents.RAR.getType());
        // <<<<<<<<<<<<<<<<<<<<<<<<
//        Upload.uploadAndCreateFolder("RAR.RAR", "nova pasta", "aa.rar", TypeContents.RAR.getType());
    }

}
