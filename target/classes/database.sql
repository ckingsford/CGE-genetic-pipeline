CREATE TABLE DiseaseInfo (
				diseaseId BIGINT NOT NULL AUTO_INCREMENT,
				diseaseName TEXT,
				diseaseDesc TEXT,
				otherNames TEXT,
				externalIds TEXT,
				source TEXT,
				creationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
				PRIMARY KEY (diseaseId)
			);


CREATE TABLE DrugInfo (
				drugId INT NOT NULL AUTO_INCREMENT,
				drugName TEXT,
				doseGuidelines TEXT,
				externalIds TEXT,
				source TEXT,
				creationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
				PRIMARY KEY (drugId)
			);

CREATE TABLE ProteinInfo (
				proteinId BIGINT NOT NULL AUTO_INCREMENT,
				proteinCode TEXT,
				proteinName TEXT,
				otherNames TEXT,
				source TEXT,
				creationTime TIMESTAMP,
				PRIMARY KEY (proteinId)
			);


CREATE TABLE PathwayInfo (
				pathwayId BIGINT NOT NULL AUTO_INCREMENT,
				pathwayName TEXT,
				pathwayCategory TEXT,
				compounds TEXT,
				relatedPathways TEXT,
				source TEXT,
				creationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
				updationTime TIMESTAMP,
				PRIMARY KEY (pathwayId)
			);
CREATE TABLE GeneInfo (
				geneId BIGINT NOT NULL, #EntrenzId
				geneCode TEXT,
				geneLocationId BIGINT,
				geneName TEXT,
				otherNames TEXT,
				otherCodes TEXT,
				externalIds TEXT,
				phenotypeId TEXT,
				source TEXT,
				creationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
				PRIMARY KEY (geneId)
			);

CREATE TABLE PhenotypeInfo (
				phenotypeId BIGINT NOT NULL AUTO_INCREMENT,
				geneId BIGINT NOT NULL,
				phenotypeName TEXT,
				phenotypeDesc TEXT,
				externalIds TEXT,
				source TEXT,
				creationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
				PRIMARY KEY (phenotypeId)
			);

CREATE TABLE GeneLocationInfo (	
				locationId BIGINT  NOT NULL AUTO_INCREMENT,			
				geneId BIGINT, #EntrenzId
				chromosome TEXT,
				start BIGINT,
				end BIGINT,
				creationTime TIMESTAMP,
				PRIMARY KEY (geneId),
CONSTRAINT FOREIGN KEY (geneId) REFERENCES GeneInfo(geneId) ON DELETE CASCADE ON UPDATE CASCADE
			);

CREATE TABLE GeneDiseases (
				geneId BIGINT NOT NULL, # EntrenzId
				diseaseId BIGINT NOT NULL,
				pValue DOUBLE,
				creationTime TIMESTAMP,
				PRIMARY KEY (geneId,diseaseId),
CONSTRAINT FOREIGN KEY (geneId) REFERENCES GeneInfo(geneId) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT FOREIGN KEY (diseaseId) REFERENCES DiseaseInfo(diseaseId) ON DELETE CASCADE ON UPDATE CASCADE
			);

CREATE TABLE GenotypePhenotypes (
				geneId BIGINT NOT NULL, #EntrenzId
				phenotypeId BIGINT NOT NULL,			
				pValue DOUBLE,
				creationTime TIMESTAMP,
				PRIMARY KEY (geneId,phenotypeId),
CONSTRAINT FOREIGN KEY (geneId) REFERENCES GeneInfo(geneId) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT FOREIGN KEY (phenotypeId) REFERENCES PhenotypeInfo(phenotypeId) ON DELETE CASCADE ON UPDATE CASCADE
			);


CREATE TABLE GeneDrugs (
				geneId BIGINT NOT NULL, #EntrenzId
				drugId BIGINT NOT NULL,
				externalIds TEXT,
				pValue DOUBLE,
				creationTime TIMESTAMP,
				PRIMARY KEY (geneId,drugId),
CONSTRAINT FOREIGN KEY (geneId) REFERENCES GeneInfo(geneId) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT FOREIGN KEY (drugId) REFERENCES DrugInfo(drugId) ON DELETE CASCADE ON UPDATE CASCADE
			);

CREATE TABLE GenePathways (
				geneId BIGINT NOT NULL, #EntrenzId
				pathwayId BIGINT NOT NULL,
				externalIds TEXT,
				pValue DOUBLE,
				creationTime TIMESTAMP,
				PRIMARY KEY (geneId,pathwayId),
CONSTRAINT FOREIGN KEY (geneId) REFERENCES GeneInfo(geneId) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT FOREIGN KEY (pathwayId) REFERENCES DiseaseInfo(pathwayId) ON DELETE CASCADE ON UPDATE CASCADE
			);

CREATE TABLE GeneProteins (
				geneId BIGINT NOT NULL, #EntrenzId
				proteinId BIGINT NOT NULL,
				pValue DOUBLE,
				externalIds TEXT,
				creationTime TIMESTAMP,
				PRIMARY KEY (geneId,proteinId),
CONSTRAINT FOREIGN KEY (geneId) REFERENCES GeneInfo(geneId) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT FOREIGN KEY (proteinId) REFERENCES proteinInfo(proteinId) ON DELETE CASCADE ON UPDATE CASCADE
			);


CREATE TABLE MutationGene (
				position BIGINT NOT NULL,
				rsId BIGINT NOT NULL,
				chromosomeNo varchar(5),
				geneId	BIGINT NOT NULL, #EntrenzId			
				externalIds TEXT,
				creationTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,			
				PRIMARY KEY (position,rsId,geneId)

			);








