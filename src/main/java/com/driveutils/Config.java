package com.driveutils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Collections;
import java.util.List;

/** Biblioteca para facilitar a comunicação com os serviços do google drive
* @author Guilherme Kirsch
* */

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Config {
    private static final String APPLICATION_NAME = "Animati";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Diretorio onde vai ser armazenado o token de autenticacao.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final String CREDENTIALS_FILE_PATH = "etc/animati/exp-laudo-prontuario";
    /**
     * Global instancia dos scopos para as operações
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Carrega as chaves secretas do arquivo json
        InputStream credentialPath = new FileInputStream(CREDENTIALS_FILE_PATH);
        if (credentialPath == null) {
            log.error("Credencias nao encontradas: {}", credentialPath.toString());
            throw new FileNotFoundException();
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(credentialPath));

        // Constroi a requisicao de autenticacao e guarda o token na pasta
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH))).setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        return credential;
    }

    //constroi uma autorização para o cliente
    @Bean
    protected static Drive buildDrive() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName(APPLICATION_NAME).build();

        } catch (Exception e) {
            UtilMessageError.printMessageError(e);
            return null;
        }

    }


}
