from django.db import models

# Analyse class
class Analyse(models.Model):
    nom = models.CharField(max_length=255)
    description = models.TextField()
    idFkLaboratoire = models.IntegerField(blank=False, null=False)
    
    def __str__(self):
        return self.nom