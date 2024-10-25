from rest_framework import serializers
from ..models.Analyse import Analyse


class AnalyseGenericSerializer(serializers.ModelSerializer):
    class Meta:
        model = Analyse

        fields = "__all__"


class UpdateAnalyseSerializer(serializers.Serializer):
    nom = serializers.CharField(required=False)
    description = serializers.CharField(required=False)
    id_fk_laboratoire = serializers.IntegerField(required=False)

    def update(self, instance: Analyse, validated_data: dict[str:any]):
        if "nom" in validated_data:
            instance.nom = validated_data.get("nom")
        if "description" in validated_data:
            instance.description = validated_data.get("description")
        if "id_fk_laboratoire" in validated_data:
            instance.id_fk_laboratoire = validated_data.get("id_fk_laboratoire")

        instance.save()

        return instance
