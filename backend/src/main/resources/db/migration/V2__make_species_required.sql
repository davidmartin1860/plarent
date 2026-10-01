UPDATE plants SET species = 'Unknown' WHERE species IS NULL OR btrim(species) = '';

ALTER TABLE plants ALTER COLUMN species SET NOT NULL;
