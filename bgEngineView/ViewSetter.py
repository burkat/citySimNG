import os
import wx
from CreatorView import CreatorView
from MenuView import MenuView
from ExchangeView import ExchangeView
from MapView import MapView
from TutorialView import TutorialView
import pygame


class MyFrame(wx.Frame):
    def __init__(self, parent, ID, strTitle, tplSize, sender):
        wx.Frame.__init__(self, parent, ID, strTitle, size=tplSize)

        self.views = {
            "Menu": MenuView(self, tplSize, "Menu", "music/NWN2.mp3", sender),
            "Creator": CreatorView(self, tplSize, "Creator", "music/Ret Xed OST.mp3", sender),
            "Exchange": ExchangeView(self, tplSize, "Exchange", sender),
            "Map": MapView(self, tplSize, "Map", sender),
            "Tutorial": TutorialView(self, tplSize, "Tutorial", sender = sender)
        }

        for name in self.views:
            self.views[name].Hide()
        # ^ hiding every view, so they will react on Show() later
        # (otherwise the first view to run will be inactive, i.e. no
        # EVT_SHOW event shall be triggered for the first view to  be seen)
        
        self.ShowFullScreen(True)
        self.currentViewName = "Menu"
        self.setView("Menu")

    def passMsgToCurrentView(self, msg):
        msgParts = msg.split("@")
        # ^ here we use split() methid having "@" separator, because arguments in the message may be texts containing spaces
        if msg.startswith("SetView"):
            self.setView(msgParts[1])
            # ^ controller told us explicitely to change the current view
        else:
            # msg is in the form: ViewName@Params
            self.views[msgParts[0]].readMsg("@".join(msgParts[1:]))

    def setView(self, viewName):
        objToRun = None
        for name in self.views:
            if name == viewName:    
                objToRun = self.views[name]
            else:
                self.views[name].Hide()
        objToRun.Show()

    def closeWindow(self, event):
        self.Destroy()



