from rest_framework import serializers
from ..models.Analyse import Analyse


class AnalyseSerializer(serializers.ModelSerializer):
    class Meta:
        model = Analyse
        fields = [
            "id",
            "nom",
            "description",
            "idFkLaboratoire",
            "created_at",
            "updated_at",
        ]
