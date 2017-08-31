import uuid

import pygame
from RelativePaths import relative_textures_path

from MapView.Consts import BUILDINGS_PANEL_TEXTURE, ARROW_BUTTON_WIDTH, ARROW_BUTTON_HEIGHT, \
    BUILDINGS_PANEL_RIGHT_ARROW_X, \
    BUILDINGS_PANEL_ARROW_Y, BUILDINGS_PANEL_LEFT_ARROW_X, BUILDING_WIDTH, BUILDING_HEIGHT, SPACE
from MapView.Items.Building import Building
from MapView.Items.Button import Button
from MapView.Panels.Panel import Panel


class BuildingsPanel(Panel):
    """ This class represents an instance of panel containing buildings. """
    def __init__(self, pos_x, pos_y, width, height, surface, buildings_data):
        """ Constructor.

        :param pos_x: x position on screen
        :param pos_y: y position on screen
        :param width: panel's width
        :param height: panel's height
        :param surface: surface on which panel should be drawn
        :param buildings_data: information about buildings available in game
        """
        Panel.__init__(self, pos_x, pos_y, width, height, BUILDINGS_PANEL_TEXTURE, surface)
        self.buildings_data = buildings_data
        self.buildings_sprites = pygame.sprite.Group()
        self.curr_page = 1
        self.last_page = 1
        self.page_buildings = {}

        self.right_arrow = Button(BUILDINGS_PANEL_RIGHT_ARROW_X * self.width + self.pos_x,
                                  BUILDINGS_PANEL_ARROW_Y * self.height + self.pos_y,
                                  ARROW_BUTTON_WIDTH * self.width, ARROW_BUTTON_HEIGHT * self.height,
                                  relative_textures_path + "RightArrow.png", self.scroll_building_panel_right, self)

        self.left_arrow = Button(self.pos_x + BUILDINGS_PANEL_LEFT_ARROW_X * self.width,
                                 self.pos_y + BUILDINGS_PANEL_ARROW_Y * self.height,
                                 ARROW_BUTTON_WIDTH * self.width, ARROW_BUTTON_HEIGHT * self.height,
                                 relative_textures_path + "LeftArrow.png", self.scroll_building_panel_left, self)

        self.parse_buildings_data()

    def draw(self):
        """ Draw buildings panel with arrows for changing pages and buildings from current page.
        Before drawing in panel it is being cleaned. """
        self.clean()
        self.panels_surface.blit(self.right_arrow.image, (self.right_arrow.rect[0] - self.pos_x,
                                                          self.right_arrow.rect[1] - self.pos_y))
        self.panels_surface.blit(self.left_arrow.image, (self.left_arrow.rect[0] - self.pos_x,
                                                         self.left_arrow.rect[1] - self.pos_y))
        self.draw_buildings_in_buildings_panel()
        self.surface.blit(self.panels_surface, (self.pos_x, self.pos_y))

    def parse_buildings_data(self):
        """ Parse information about buildings available in game sent by model - split buildings into pages and
        create sprite for each building. """
        curr_x, curr_y = 0, 0
        for building in self.buildings_data:
            # we have to go to next line
            if curr_x + BUILDING_WIDTH * self.width > self.width:
                curr_x = 0
                curr_y = curr_y + BUILDING_HEIGHT * self.height + SPACE

            # we have to go to next page
            if curr_y + BUILDING_HEIGHT * self.height > BUILDINGS_PANEL_ARROW_Y * self.height:
                curr_y = 0
                self.curr_page += 1
                self.last_page = self.curr_page

            building_sprite = Building(building["name"], uuid.uuid4().__str__(), building["texturePath"],
                                       building["resourcesCost"], building["consumes"], building["produces"],
                                       curr_x + self.pos_x, curr_y + self.pos_y, int(BUILDING_WIDTH * self.width),
                                       int(BUILDING_HEIGHT * self.height))
            curr_x += BUILDING_WIDTH * self.width + SPACE
            if str(self.curr_page) in self.page_buildings:
                self.page_buildings[str(self.curr_page)].append(building_sprite)
            else:
                self.page_buildings[str(self.curr_page)] = [building_sprite]
        self.curr_page = 1

    def draw_buildings_in_buildings_panel(self):
        """ Draw buildings from current page. """
        self.buildings_sprites = pygame.sprite.Group()
        for building in self.page_buildings[str(self.curr_page)]:
            self.panels_surface.blit(building.image, (building.pos_x - self.pos_x, building.pos_y - self.pos_y))
            self.buildings_sprites.add(building)

    def scroll_building_panel_right(self):
        """ Go to next page with buildings. """
        if self.curr_page < self.last_page:
            self.curr_page += 1
            # self.draw()

    def scroll_building_panel_left(self):
        """ Go to previous page with buildings. """
        if self.curr_page > 1:
            self.curr_page -= 1
            # self.draw()