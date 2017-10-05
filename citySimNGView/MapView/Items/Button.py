import pygame
from MapView.CustomSprites.BasicSprite import BasicSprite
from CreatorView.RelativePaths import relative_music_path


class Button(BasicSprite):
    """ This class represents an instance of button. """
    def __init__(self, pos_x, pos_y, width, height, texture, action, panel, popup_text, texture_rotation=0):
        """ Constructor.

        :param pos_x: x position on screen
        :param pos_y: y position on screen
        :param width: button's width
        :param height: button's height
        :param texture: path to button's texture
        :param action: action performed when button is released
        :param panel: panel in which button is located
        :param popup_text: text displayed in popup
        :param texture_rotation: angle of texture rotation
        """
        BasicSprite.__init__(self, pos_x, pos_y, width, height, texture, popup_text, texture_rotation=texture_rotation)
        self.action = action
        self.panel = panel

        self.args = None

    def click_button(self, *args):
        """ Change button size to look like it is pushed and store arguments for later action.

        :param args: arguments stored to perform action when player will release button.
        """
        self.image = pygame.transform.scale(self.image, (self.width/2, self.height/2))
        self.rect = self.image.get_rect(center=self.rect.center)
        self.args = args

    def release_button(self):
        """ Perform appropriate action and restore button to normal size. """
        self.action(*self.args)
        self.image = pygame.image.load(self.texture_path)
        self.image = pygame.transform.rotate(self.image, self.texture_rotation)
        self.image = pygame.transform.scale(self.image, (self.width, self.height))
        self.rect = self.image.get_rect(topleft=(self.pos_x, self.pos_y))

    def set_texture(self, texture):
        """ Load texture for button from file, scale it and get it's rect.

        :param texture: path to button's texture
        """
        self.texture = texture
        self.load_texture(self.texture_rotation)


