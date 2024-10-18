/*
  Warnings:

  - The `logo` column on the `Laboratoire` table would be dropped and recreated. This will lead to data loss if there is data in the column.

*/
-- AlterTable
ALTER TABLE "Laboratoire" DROP COLUMN "logo",
ADD COLUMN     "logo" INTEGER[];
