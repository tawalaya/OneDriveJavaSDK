# OneDrive SDK

## Quickstart
Register your app. For more info see [Registration](#registration)

```java
OneDriveSDK sdk = OneDriveFactory.createOneDriveSDK(
    //provide the client credentials of your application 
    "[clientId]", "[clientSecret]",             
    //the return url that will be redirected to after the authentication step
    "[returnUrl]",                              
    //define the scopes of your application; more info at:
    // https://dev.onedrive.com/auth/msa_oauth.htm#authentication-scopes
    OneDriveScope.READWRITE);                   

//handle response on your returnUrl to get the oAuthCode, it is included in the redirectUrl               
sdk.authenticate("[oAuthCode]"); //authenticate current session

//fetches the root folder of the OneDrive
OneFolder rootFolder = sdk.getRootFolder();             
System.out.println(rootFolder);
```

A console client with several command can be found in de.tuberlin.onedrivesdk.example.ConsoleClient.

## Registration
1. Register your client application on [Mirosoft Developers](http://go.microsoft.com/fwlink/p/?LinkId=193157)
2. The drive is created at the first login. Login into your account in the web browser, otherwise you will get an authentication error if you try to run the SDK with your credentials.
    - The user have to be logged in at least once to use your application.  
3. (optional) Register a website  if you want to use this SDK in a web app. Otherwise you can register http://localhost.
4. A development authentication token can be obtained on [OneDrive authentication](https://dev.onedrive.com/auth/msa_oauth.htm). 
5. More details can be found [here](https://dev.onedrive.com/app-registration.htm)

## Installation
Download our latest release [here](https://github.com/tawalaya/OneDriveJavaSDK/releases) 

Alternatively you can use maven. We host a maven artifact on github. The following code snippet can be used to for dependency management with maven.
```xml
...
<repositories>
    <repository>
        <id>de.tuberlin</id>
        <url>https://raw.github.com/tawalaya/onedrivejavasdk/mvn-repo/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>de.tuberlin</groupId>
        <artifactId>onedrivesdk</artifactId>
        <version>0.0.4-SNAPSHOT</version>
    </dependency>
</dependencies>
...
```
## Recommended CodeFlow for user authentication
 The first time a new user uses your application he or she needs to go to the authentication URL. The following code gets that URL:
 
 ```java 
String loginUrl = sdk.getAuthenticationURL();   
 ```
 
 The user than has to allow your application and gets a oAuth2.0 token, depending on your application you can automatically receive it via the previous provided redirect URL or need the user to provide it manually to your application.  

 ```java 
sdk.authenticate("[oAuthToken]");  
 ```

 At this point you can persist the session if you want by storing the refresh token:

 ```java 
String refreshToken = sdk.getRefreshToken();
 ```

 Please handle this token with care it is equivalent to a user password and depending on the rights you requested it can be used to remove elements in the OneDrive of the given user.

 The next time your application starts, you can use the saved refreshToken to authenticate the session using the following code:

  ```java 
 sdk.authenticateWithRefreshToken("[refreshToken]");   
  ```
 
## Featuers

### TextFile Configuration
#### [Credentials](http://tawalaya.github.io/OneDriveJavaSDK/docs/de/tuberlin/onedrivesdk/common/OneDriveCredentials.html)
Instead of manually providing the clientID and clientSecret every time you create the SDK handle and store them within your source code you can use a *credentails.prperties* file. The File needs to be stored in the root folder of your application.

It should look somewhat like this:
``` 
clientId = ...
clientSecret = ...
```

If you have such a file you can also use a simpler function to create a session:

```java
OneDriveSDK sdk = OneDriveFactory.
    createOneDriveSDK("[returnUrl]",OneDriveScope.READWRITE);  
```

#### Logging
This library uses log4j2 to handle logging. Therefore you can specify a log4j configuration to see helpful information about the inner working of the SDK.

More information can be found [here](http://logging.apache.org/log4j/2.x/manual/configuration.html) 

### Automatic Refresh
The SDK provides the ability to continuously refresh the authentication token. This is useful if your application will need to use OneDrive over extended period of time.
After the application went through the  authentication steps described [here](#recommended-codeflow-for-user-authentication) the you can call the`sdk.startSessionAutoRefresh()` method. Now keep in mind that you need to terminate that thread if you want your application to close normally. To do that you can call the `sdk.disconnect()` method.

### Offered Operations
<!-- TODO add JDOC URLs -->
[SDK](http://tawalaya.github.io/OneDriveJavaSDK/docs/de/tuberlin/onedrivesdk/OneDriveSDK.html):
* get file or folder by id or path:
The SDK provides multiple methods to get ether a file or folder by its path relative to / (the root folder) or by its unique id which OneDrive generates.

[File](http://tawalaya.github.io/OneDriveJavaSDK/docs/de/tuberlin/onedrivesdk/file/OneFile.html)/[Folder](http://tawalaya.github.io/OneDriveJavaSDK/docs/de/tuberlin/onedrivesdk/folder/OneFolder.html):
* traverse folders:
Each folder object has a getChildren(), getChildFolder() and a getChildFiles() method to list all available children and or folder. This can be used to move to a lower level of the file system. Every folder also has the getParentFolder() method, which can be used to move up.
* get metadata:
    - getName
    - getSize
    - getWeburl()
    - getRawJson() 
        if your application wants to do some additional parsing of the API responses
* refresh metadata:
   this can be done to re-fetch all meta data from the OneDrive cloud
* delete files or folder
* create folder

Blocking Operations:
* copy file
* move file
* download file
* upload file 


### Error handling 
There are two exceptions that can occur while using the SDK. The one that can occur most often is the [OneDriveException](http://tawalaya.github.io/OneDriveJavaSDK/docs/de/tuberlin/onedrivesdk/OneDriveException.html). This exception will be thrown in most cases if the API refused a command send by the SDK. Please look for solutions on the developer side of [Microsoft](https://dev.onedrive.com/) for explanations. The other exception that can occur is the 
[OneDriveAuthenticationException](http://tawalaya.github.io/OneDriveJavaSDK/docs/de/tuberlin/onedrivesdk/networking/OneDriveAuthenticationException.html). This exception will be thrown if the session that the SDK is using is no longer valid. Ether use the `sdk.authenticateWithRefreshToken(sdk.getRefreshToken())` or make sure to enable the [automatic refresh](#automatic-refresh)

## Additional Information
* for more information about availible classes and methods visit the [javadoc pages](http://tawalaya.github.io/OneDriveJavaSDK/docs/)
* for more information about the underling service visit the [dev.onedrive.com](https://dev.onedrive.com/README.htm) site.

## History
This project was developed at TU Berlin during the [Cloud Prototyping](http://www.ise.tu-berlin.de/menue/teaching/summer_term_2015/cloud_prototyping/parameter/en/) course in the summer term of 2015 from [Tim Hinkes](https://github.com/Timmeey), [Andreas Salzmann](https://github.com/andi3) and [Sebastian Werner](https://github.com/tawalaya) under the supervision of [Markus Klems](http://www.ise.tu-berlin.de/menue/team/markus_klems_dipl-wi-ing/parameter/en/).
