package com.driveutils;

import java.util.Map;

public interface Upload {
    /*
    * UploadImpl de arquivo, criando uma pasta para incluir o arquivo
    * */
    void uploadAndCreateFolder(String fileName, String folderName, String path, String type);

    /*
    * UploadImpl simples de arquivo
    * */
    void upload(String fileName, String path, String type);

    void uploadInExistentFolder(String nameFolder, String fileName, String type, String path);

    /*
    * Lista as pastas existentes no drive
    * */ Map<String, String> searchPath();

    /*
    * Cria pasta
    * */
    String createFolder(String name);
}
