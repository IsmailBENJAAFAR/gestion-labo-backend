# Generated by Django 5.1 on 2024-10-24 19:12

import django.utils.timezone
from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Analyse',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('created_at', models.DateTimeField(db_index=True, default=django.utils.timezone.now)),
                ('updated_at', models.DateTimeField(auto_now=True)),
                ('nom', models.CharField(max_length=255)),
                ('description', models.TextField()),
                ('id_fk_laboratoire', models.IntegerField()),
            ],
            options={
                'abstract': False,
            },
        ),
    ]
