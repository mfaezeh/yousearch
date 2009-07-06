package it.unitn.datamining.yousearch;

public class YouDTO2ARFF {
	private String output;
	private YouSearchResult result;
	private YouTagGroupSimilarity similarityAgent;
	private YouTagValidator tagValidator;

	private void initARFF() {
		System.out.println("initARFF");
		if (this.result == null)
			System.exit(0);

		this.output = new String();

		this.output += "@relation tags\n\n";
		this.output += "@attribute similarity numeric\n";
		this.output += "@attribute sum numeric\n";
		this.output += "@attribute tags string\n";
		this.output += "@attribute video_id string\n\n";
		this.output += "@data\n\n";

	}

	private void doConversion() {
		this.similarityAgent = new YouTagGroupSimilarity(this.result
				.getKeyword());
		// this.stemmer = new SnowballStemmer();
		this.tagValidator = new YouTagValidator();

		// iterations over videos
		int rSize = this.result.getSize();
		for (int i = 0; i < rSize; i++) {

			YouSearchEntry tmpEntry = result.getItem(i);
			String[] unfilteredTag = tmpEntry.getTags().split(" ");
			String filteredTag = new String();
			YouSimilarityDTO similarityDTO;

			for (int j = 0; j < unfilteredTag.length; j++)
				if (this.tagValidator.isValid(unfilteredTag[j]))
					filteredTag += " " + unfilteredTag[j];

			// similarityDTO = new YouSimilarityDTO();
			similarityDTO = this.similarityAgent.getSimilarity(filteredTag
					.split(" "), tmpEntry.getVideoId());
			// System.out.println(arrayTag.size());
			appendToOutput(filteredTag.split(" "), similarityDTO);
		}
	}

	private void appendToOutput(String[] tags, YouSimilarityDTO value) {

		String toFile = "";

		for (int i = 0; i < tags.length; i++)
			toFile += tags[i].toString() + " ";

		this.output += "\n" + String.valueOf(value.similarity) + ","
				+ String.valueOf(value.sum) + "," + "\"" + toFile.trim() + "\""
				+ "," + "\"" + String.valueOf(value.videoId) + "\"";

	}

	public String getARFF() {
		return this.output;
	}

	public YouDTO2ARFF(YouSearchResult result) {
		this.result = result;
		initARFF();
		doConversion();
	}

}
