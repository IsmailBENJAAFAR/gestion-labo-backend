# import datetime
# from django.db import models

# # Laboratoire class
# class Laboratoire(models.Model):
#     nom = models.CharField(max_length=255)
#     nrc = models.IntegerField()
#     logo = models.ImageField(upload_to='my_api/media/')
#     active = models.BooleanField(default=True)
#     date_activation = models.DateField(default=datetime.date.today())
    
#     def __str__(self):
#         return self.nom