from my_api.models.Analyse import Analyse
from my_api.serializers.serializers import UpdateAnalyseSerializer
from rest_framework import status


def update_analyse(data: dict[str:any], id: str) -> dict[str, any]:
    """
    Summary:
    Updates an Analyse using its id, the json response needs to be in this format :
                {
                    "id":id,
                    "nom":nom,
                    "description":description,
                    "idFkLaboratoire":idFkLaboratoire
                }
    Args:
        method (str): Http method from the request
        data (dict[str:any]): json data from the request
        id (int): id from the url (this might be removed at some later update)

    Returns: (dict[str, any] | None)
    """
    try:
        analyse = Analyse.objects.get(id=id)
    except:
        return {
            "response_data": {},
            "response_status": status.HTTP_404_NOT_FOUND,
        }
    analyse_serializer = UpdateAnalyseSerializer(analyse, data=data)
    if analyse_serializer.is_valid():
        analyse_serializer.save()
        return {"response_data": {}, "response_status": status.HTTP_200_OK}
    else:
        return {"response_data": {}, "response_status": status.HTTP_400_BAD_REQUEST}
