from django.db import models
from django.utils import timezone


class BaseModel(models.Model):
    """
    An abstract Model that adds the `created_at` and `updated_at` fields
    Can be used when building other Models, and it is generally a good practice to use them
    """

    created_at = models.DateTimeField(db_index=True, default=timezone.now)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        abstract = True
