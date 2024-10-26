from django.db import models

from .BaseModel import BaseModel


class Analyse(BaseModel):
    nom = models.CharField(max_length=255)
    description = models.TextField()
    id_fk_laboratoire = models.IntegerField(blank=False, null=False)

    def __str__(self):
        return self.nom
