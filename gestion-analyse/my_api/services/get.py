from rest_framework import status
from my_api.models.Analyse import Analyse
from my_api.serializers.serializers import AnalyseGenericSerializer


def get_all()-> dict[str, any]:
    """
        Gets all the analyses from the DB
        
    Args:
        method (str): HTTP method used, usually passed from query.method when calling this function

    Returns:
        _type_: (dict[str, Any] | None)
    """
    analyses = Analyse.objects.all()
    analyse_serializer = AnalyseGenericSerializer(analyses, many=True)
    return {
        "response_data": analyse_serializer.data,
        "response_status": status.HTTP_200_OK,
    }


def get_by_id(id:int)-> dict[str, any]:
    """
        Get analyse by id
        
    Args:
        method (str): HTTP method used, usually passed from query.method when calling this function
        id (int): the analyse id

    Returns:
        _type_: (dict[str, Any] | None)
    """
    try:
        analyse = Analyse.objects.get(id=id)
    except:
        return {
            "response_data": {},
            "response_status": status.HTTP_404_NOT_FOUND,
        }
    analyse_serializer = AnalyseGenericSerializer(analyse)
    return {
        "response_data": analyse_serializer.data,
        "response_status": status.HTTP_200_OK,
    }
