from my_api.serializers.serializers import AnalyseGenericSerializer
from rest_framework import status


def create_analyse(data: dict[str, any]) -> dict[str, any]:
    analyse_serializer = AnalyseGenericSerializer(data=data)
    if analyse_serializer.is_valid():
        analyse_serializer.save()
        return {"response_data": {}, "response_status": status.HTTP_201_CREATED}
    else:
        return {"response_data": {}, "response_status": status.HTTP_400_BAD_REQUEST}
