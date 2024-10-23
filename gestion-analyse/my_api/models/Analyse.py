from django.db import models

# python imports can be strange sometimes
from .BaseModel import BaseModel


# Analyse class
class Analyse(BaseModel):
    nom = models.CharField(max_length=255)
    description = models.TextField()
    idFkLaboratoire = models.IntegerField(blank=False, null=False)

    def __str__(self):
        return self.nom
