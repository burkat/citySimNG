import json
import os
import threading
import traceback
import pygame
import thread

import wx
from CreatorView.RelativePaths import relative_music_path, relative_textures_path

from Consts import RESOURCES_PANEL_SIZE, PURPLE, FONT, TEXT_PANEL_HEIGHT, \
    TEXT_PANEL_WIDTH, MENU_BUTTON_WIDTH, NAVIGATION_PANEL_WIDTH, INFO_PANEL_WIDTH, TEXT_PANEL_FONT_SIZE
from Game import Game
from GameThread import GameThread
from Utils import draw_text
from Converter import Converter


class MapView(wx.Panel):
    """ This class represents an instance of map view. It is responsible for communication with model. """
    map_view_initialized = False
    can_afford_on_building = False
    last_res_info = None
    condition = threading.Condition()

    def __init__(self, parent, size, name, sender, music_path=relative_music_path + "TwoMandolins.mp3"):
        """ Constructor.

        :param parent:
        :param size: game screen size
        :param name:
        :param sender:
        :param music_path: path to music
        """
        # call base class constructor
        wx.Panel.__init__(self, parent=parent, size=size)

        # set class fields
        self.parent = parent
        self.width = size[0]
        self.height = size[1]
        self.screen_height = size[1]
        self.name = name
        self.sender = sender
        self.music_path = music_path

        # bind EVT_SHOW to onShow() function
        self.Bind(wx.EVT_SHOW, self.on_show, self)

        # add buttons
        self.init_buttons()

        style = wx.TE_MULTILINE | wx.TE_READONLY
        self.log = wx.TextCtrl(self, wx.ID_ANY, size=(size[0] * TEXT_PANEL_WIDTH, TEXT_PANEL_HEIGHT * size[1]),
                               style=style, pos=(self.width * NAVIGATION_PANEL_WIDTH + self.width * INFO_PANEL_WIDTH,
                                                 int(size[1] - TEXT_PANEL_HEIGHT * size[1])))
        font = wx.Font(TEXT_PANEL_FONT_SIZE, wx.MODERN, wx.NORMAL, wx.NORMAL, False, FONT)
        self.log.SetFont(font)

        self.sizer = wx.BoxSizer(wx.VERTICAL)
        self.sizer.Add(self.log, 1, wx.ALL | wx.EXPAND, 5)
        self.SetSizer(self.sizer)

    def init_buttons(self):
        """ Function initializing buttons. """
        menu_btn = wx.Button(self, label="Menu", pos=(self.width - MENU_BUTTON_WIDTH * self.width,
                                                      self.screen_height - self.screen_height * TEXT_PANEL_HEIGHT),
                             size=(MENU_BUTTON_WIDTH * self.width, self.screen_height * TEXT_PANEL_HEIGHT))
        self.Bind(wx.EVT_BUTTON, self.ret_to_menu, menu_btn)

    def on_show(self, event):
        """ Function receiving events sent to map view. """
        if event.GetShow():
            self.init_view()
            try:
                pygame.mixer.init()
            except Exception:
                print "Problem with music"
        # else:
        #     try:
        #         pygame.quit()
        #     except Exception:
        #         print "first appearance of MapView: pygame not initialized in map"

    def init_view(self):
        """ Function initializing map view. """
        self.hackPygame()
        pygame.init()
        pygame.display.init()
        self.sender.entry_point.getMapPresenter().viewInitialized()

    def hackPygame(self):
        global pygame
        os.environ['SDL_WINDOWID'] = str(self.GetHandle())
        os.environ['SDL_VIDEODRIVER'] = 'windib'
        import pygame  # this has to happen after setting the environment variables.
        pygame.init()
        pygame.quit()
# =================================================================================================================== #
# Communication with model
# =================================================================================================================== #

# =================================================================================================================== #
# Functions sending messages to model
# =================================================================================================================== #

    def ret_to_menu(self, event):
        """ Send node change message to model. """
        self.game.game_on = False
        self.game.listener_thread.join()
        self.map_view_initialized = False
        self.sender.entry_point.getMapPresenter().goToMenu()

    def erected_building(self, building):
        """ Send message to model that new building has been erected. """
        result = self.sender.entry_point.getMapPresenter().placeBuilding(building.name, building.id)
        self.game.resources_panel.resources_values = \
            Converter().convertJavaMapToDict(result.getActualResourcesValues())
        self.game.resources_panel.resources_incomes = Converter().convertJavaMapToDict(
            result.getActualResourcesIncomes())
        self.game.resources_panel.resources_consumption = Converter().convertJavaMapToDict(
            result.getActualResourcesConsumption())
        self.game.resources_panel.resources_balance = Converter().convertJavaMapToDict(result.getResourcesBalance())
        self.game.resources_panel.curr_dwellers_amount = result.getNeededDwellers()
        self.game.resources_panel.curr_max_dwellers_amount = result.getAvailableDwellers()
        self.game.buildings_panel.enable_buildings(result.getEnabledBuildings())
        building.working_dwellers = result.getWorkingDwellers()

    def check_if_can_afford(self, building):
        """ Send message to model with the inquiry if player has enough resources to erect building and
        wait for response.

        :param building: building that player wants to erect
        :return: response from model telling if player can afford to erect building
        """
        return self.sender.entry_point.getMapPresenter().checkIfCanAffordOnBuilding(building.name)

    def deleted_building(self, building_id):
        """ Send message to model that building has been deleted.

        :param building_id: id of building that will be deleted
        """
        result = self.sender.entry_point.getMapPresenter().deleteBuilding(building_id)
        self.game.resources_panel.resources_values = \
            Converter().convertJavaMapToDict(result.getActualResourcesValues())
        self.game.resources_panel.resources_incomes = Converter().convertJavaMapToDict(
            result.getActualResourcesIncomes())
        self.game.resources_panel.resources_consumption = Converter().convertJavaMapToDict(
            result.getActualResourcesConsumption())
        self.game.resources_panel.resources_balance = Converter().convertJavaMapToDict(result.getResourcesBalance())
        self.game.resources_panel.curr_dwellers_amount = result.getNeededDwellers()
        self.game.resources_panel.curr_max_dwellers_amount = result.getAvailableDwellers()

    def stop_production(self, building_id):
        """ Stop production in given building.

        :param building_id: id of the building where production will be stopped
        """
        result = self.sender.entry_point.getMapPresenter().stopProduction(building_id)
        self.game.resources_panel.resources_values = \
            Converter().convertJavaMapToDict(result.getActualResourcesValues())
        self.game.resources_panel.resources_incomes = Converter().convertJavaMapToDict(
            result.getActualResourcesIncomes())
        self.game.resources_panel.resources_consumption = Converter().convertJavaMapToDict(
            result.getActualResourcesConsumption())
        self.game.resources_panel.resources_balance = Converter().convertJavaMapToDict(result.getResourcesBalance())
        self.game.resources_panel.curr_dwellers_amount = result.getNeededDwellers()
        self.game.resources_panel.curr_max_dwellers_amount = result.getAvailableDwellers()
        self.game.info_panel.curr_building.is_running = result.isRunning()
        self.game.info_panel.set_stop_production_button_texture()

    def set_dwellers_working_in_building(self, building_id):
        """ Get number of dwellers working in given building.

        :param building: building for which get information
        """
        result = self.sender.entry_point.getMapPresenter().getWorkingDwellers(building_id)
        self.game.info_panel.curr_building.working_dwellers = result

    # =================================================================================================================== #
# Reading messages from model
# =================================================================================================================== #
    def init(self, resources, domestic_buildings, industrial_buildings, dwellers,
             texture_one, texture_two,  panelTexture, mp3, initial_resources_values,
             initial_resources_incomes, initial_resources_consumption,
             initial_resources_balance, available_dwellers):
        """ Initialize game -> create game instance. After creating game instance send acknowledgement to model. """
        self.music_path = relative_music_path + mp3
        pygame.mixer.music.load(self.music_path)
        pygame.mixer.music.play()
        self.game = Game(
            self.width,
            self.height,
            texture_one,
            texture_two,
            domestic_buildings,
            industrial_buildings,
            resources,
            dwellers,
            initial_resources_values,
            initial_resources_incomes,
            initial_resources_consumption,
            initial_resources_balance,
            self,
            available_dwellers)
        self.sender.entry_point.getMapPresenter().viewInitialized()

    def update_values_for_cycle_thread(self, actual_resources_values,
                                actual_resources_incomes,
                                actual_resources_consumption,
                                resources_balance, needed_dwellers,
                                available_dwellers):
        """ Update resources values """
        res_vals = Converter().convertJavaMapToDict(actual_resources_values)
        self.game.resources_panel.resources_values = res_vals
        res_incomes = Converter().convertJavaMapToDict(actual_resources_incomes)
        self.game.resources_panel.resources_incomes = res_incomes
        res_consumption = Converter().convertJavaMapToDict(actual_resources_consumption)
        self.game.resources_panel.resources_consumption = res_consumption
        res_balance = Converter().convertJavaMapToDict(resources_balance)
        self.game.resources_panel.resources_balance = res_balance
        self.game.resources_panel.curr_dwellers_amount = needed_dwellers
        self.game.resources_panel.curr_max_dwellers_amount = available_dwellers

    def update_values_for_cycle(self, actual_resources_values,
                                actual_resources_incomes,
                                actual_resources_consumption,
                                resources_balance, needed_dwellers,
                                available_dwellers):
        thread.start_new_thread(self.update_values_for_cycle_thread, ( actual_resources_values,
                                actual_resources_incomes,
                                actual_resources_consumption,
                                resources_balance, needed_dwellers,
                                available_dwellers))

    def resume_game(self):
        """ Resume game. """
        self.game.set_display_mode()
        self.game.game_on = True
        self.game.listener_thread = GameThread(self.game)
        self.game.listener_thread.start()

