# OneDrive SDK

## Quickstart
Register your app for more info see [Registration](#Registration)

```java
OneDriveSDK sdk = OneDriveFactory.createOneDriveSDK(
    //provide the client credentials of your application 
    "[clientId]", "[clientSecret]",             
    //the return url that will be redirected to after the authentication step
    "[returnUrl]",                              
    //define the scopes of your application; more info at:
    // https://dev.onedrive.com/auth/msa_oauth.htm#authentication-scopes
    OneDriveScope.READWRITE);                   

//handle response on your returnUrl to get the oAuthCode               
sdk.authenticate("[oAuthCode]"); //authenticate current session

//fetches the root folder of the OneDrive
OneFolder rootFolder = sdk.getRootFolder();             
System.out.println(rootFolder);
```

A console client with several command can be found in de.tuberlin.client.ConsoleClient.

## Registration
1. Register your client application on [https://onedrive.live.com/](https://onedrive.live.com/)
2. The drive is created at the first login. Login into your account in the web browser, otherwise you will get an authentication error if you try to run the SDK with your credentials.
    - The user have to be logged in at least once to use your application.  
3. (optional) Register a website at [OneDrive](OneDrive) if you want to use this SDK in a web app. Otherwise you can register http://localhost.
4. A development authentication token can be obtained on [https://dev.onedrive.com/auth/msa_oauth.htm](OneDrive authentication). 

## Installation
Download our latest release [here](https://github.com/tawalaya/OneDriveJavaSDK/releases) 

## Recommended CodeFlow for user authentication
 The first time a new user uses your application he or she needs to go to the authentication URL. The following code gets that URL:
 
 ```java 
String loginUrl = sdk.getAuthenticationURL();   
 ```
 
 The user than has to allow your application and gets a oAuth2.0 token, depending on your application you can automatically receive it via the previous provided redirect URL or need the user to provide it manually to your application.  

 ```java 
sdk.authenticate("[oAuthToken]");  
 ```

 At this point you can persist the session if you want by storing the redirect token:

 ```java 
String refreshToken = sdk.getRefreshToken();
 ```

 Please handle this token with care it is equivalent with a user password and depending on the rights you requested it can be used to remove elements in the OneDrive of the given user.

 The next time your application stats you can use the saved refreshToken to authenticate the session using the following code:

  ```java 
 sdk.authenticateWithRefreshToken("[refreshToken]");   
  ```
 
## Featuers

### TextFile Configuration
#### [Credentials](http://tawalaya.github.io/OneDriveJavaSDK/docs/de/tuberlin/onedrivesdk/common/OneDriveCredentials.html)
Instead of manually providing the clientID and clientSecret every time you create the SDK handle and store them within your source code you can use a *credentails.prperties* file. The File needs to be stored in the resource folder of your application.

It should look somewhat like that:
``` 
clientId = ...
clientSecret = ...
```

If you have such a file you can also use a simpler function to create a session like this:

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
* get file or folder by id or path
    The SDK provides multiple methods to get ether a file or folder by its path relative to / (the root folder) or by its unique id witch onedrive generates.

[File](http://tawalaya.github.io/OneDriveJavaSDK/docs/de/tuberlin/onedrivesdk/file/OneFile.html)/[Folder](http://tawalaya.github.io/OneDriveJavaSDK/docs/de/tuberlin/onedrivesdk/folder/OneFolder.html):
* traverse folders 
    Each folder object has a getChildren() and getChildFolder() method to list all available children and or folder this can be used to move to lower level of the file system. each folder also has the getParentFolder() method witch can be used to move up.
* get metadata
    - getName
    - getSize
    - getWeburl()
    - getRawJson() 
        if your application wants to do some additional parsing of the API responses
* refresh metadata
   this can be done to re-fetch all meta data from the OneDrive cloud
* delete file or folder
* create folder

Blocking Operations:
* copy file
* move file
* download file
* upload file 


### Error handling 
There are two exceptions that can occur while using the SDK. The one that can occur most often is the [OneDriveException](http://tawalaya.github.io/OneDriveJavaSDK/docs/de/tuberlin/onedrivesdk/OneDriveException.html). This exception will be thrown in most cases if the API refused a command send by the SDK. Please look for solutions on the developer side of [Microsoft](https://dev.onedrive.com/) for explanations. The other exception that can occur can be the 
[OneDriveAuthenticationException](http://tawalaya.github.io/OneDriveJavaSDK/docs/de/tuberlin/onedrivesdk/networking/OneDriveAuthenticationException.html) this will be thrown if the session that the SDK is using is no longer valid. Ether use the `sdk.authenticateWithRefreshToken(sdk.getRefreshToken())` or make sure to enable the [automatic refresh](#automatic-refresh)
