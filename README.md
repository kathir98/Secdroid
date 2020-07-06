# Secdroid

Secdroid- a android app which helps in finding fishy activity that going on your android app.
Some apps we use may sent your private data to their server or some 3rd parties servers.

Those malicious activity can be found out by analysing the outgoing network traffic from the device.

To achieve that Secdroid uses two,

1. Antmonitor library - https://github.com/UCI-Networking-Group/AntMonitor
  
    It is used to intercept outgoing data traffic and presents to the user in list. In android the network traffic of whole device can be intercepted without root access by setting a tunnel interface. Antmonitor library takes that work and helps in achieving  our work easily.
  

2. Virustotal api - https://developers.virustotal.com/reference

    The files or apps in our device are scanned with the help of virustotal. Virustoal provides API for developers to use their services. Using this feature, we can find out the malicious  application present in our device. Virustotal uses various antivirus engines to scan the files, thereby files we given are scanned by many AV engines which increases the reliability.

# Note

1. Clone the project
2. And try it, feel free to customise according to your needs.
3. The signed APK is added with this repo.
