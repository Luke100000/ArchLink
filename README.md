Fork of [ForgeLink](https://github.com/Pequla/ForgeLink)

# ArchLink

Advanced Discord based whitelisting server side mod based on Architectury, thus available for Fabric and Forge.

## Requirements

In order for this mod to work you need a Discord server where you have invited
the [SuperLink Discord Bot](https://discord.com/api/oauth2/authorize?client_id=770681237622095913&permissions=8&scope=bot%20applications.commands)

Your server needs to run `forge-1.19.2-43.2.6` or newer

## Installation

Download the latest version from releases and start the server once to generate the `discord.properties` file
in your server root folder

It should look something like this:

```properties
#ForgeLink configuration file
#Sat Sep 03 10:24:45 CEST 2022
discord.guild=651476404201979926
discord.webhook=https://discord.com/api/webhooks/...
```

- `discord.guild` represents the Discord server id based on where you want whitelisting to work
- `discord.webhook` is a discord webhook url

> You can create a webhook in channel settings under integrations. When you create it, just paste the url
> into `discord.webhooks`

**That's it, now just enjoy your server!**
