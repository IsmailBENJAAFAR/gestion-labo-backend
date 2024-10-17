from django.db import models

from my_api.models.Laboratoire import Laboratoire

# Analyse class
class Analyse(models.Model):
    nom = models.CharField(max_length=255)
    description = models.TextField()
    idFkLaboratoire = models.ForeignKey(Laboratoire,
                                        on_delete=models.CASCADE,
                                        db_column="idFkLaboratoire",db_constraint=False)
    
    def __str__(self):
        return self.nom