from rest_framework import serializers
from ..models.Analyse import Analyse


class AnalyseGenericSerializer(serializers.ModelSerializer):
    class Meta:
        model = Analyse
        fields = "__all__"


class UpdateAnalyseSerializer(serializers.Serializer):
    nom = serializers.CharField(required=False)
    description = serializers.CharField(required=False)
    idFkLaboratoire = serializers.IntegerField(required=False)

    def update(self, instance: Analyse, validated_data: dict[str:any]):
        # instance.nom = validated_data
        for e in validated_data:
            if e == "nom":
                instance.nom = validated_data.get("nom")
            elif e == "description":
                instance.description = validated_data.get("description")
            elif e == "idFkLaboratoire":
                instance.idFkLaboratoire = validated_data.get("idFkLaboratoire")

        instance.save()

        return instance
