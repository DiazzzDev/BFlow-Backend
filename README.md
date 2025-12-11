# BFlow - API

Hello!

## SETUP

- Install Docker Desktop, you can install it from [Docker](https://www.docker.com/products/docker-desktop/)

- Once installed open Docker Desktop, open the app during the process, this is important.

- Open VSCode and install Dev Containers extension [here](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)

- Open the folder where this project is

- A notification will appear

- Click on the "Reopen in Container" option, then wait until everything is setup. (Around 7-10 min in the first time)

- In the process there will be a pop-up asking for your WakaTime Api Key (This is a tool that I personally use, if you dont use it just click 'Enter' and nothing will happend). If you want to use WakaTime too you can add it to your dashboards [here](https://wakatime.com/settings/api-key)

- Watch the containers in Docker Desktop, everyone has to run. (The status is now green on docker)

- Once everything is downloaded you can run the application with the following command:

```bash
    ./mvnw spring-boot:run
```

## Troubleshooting during setup

### My containers had trouble:

Try this first:
- Close all VSCode windows and Docker Desktop, 
- Reopen Docker Desktop first and then VSCode
- Open the folder project
- Click on the notification
- Wait again and see if the containers are running on Docker Desktop

If that didnt work usually is because Docker sometimes requires you to restart your machine and try the steps I wrote before.