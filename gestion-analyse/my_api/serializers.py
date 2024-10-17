from rest_framework import serializers
from .models.Analyse import Analyse
from .models.Laboratoire import Laboratoire

class AnalyseSerializer(serializers.ModelSerializer):
    class Meta:
        model = Analyse
        fields = ["id","nom","description","idFkLaboratoire"]
        
class LaboratoireSerializer(serializers.ModelSerializer):
    class Meta:
        model = Laboratoire
        fields = ["id","nom","nrc","logo","active","date_activation"]