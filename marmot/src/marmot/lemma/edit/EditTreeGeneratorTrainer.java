package marmot.lemma.edit;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marmot.lemma.Instance;
import marmot.lemma.LemmaCandidateGenerator;
import marmot.lemma.LemmaCandidateGeneratorTrainer;
import marmot.lemma.Options;
import marmot.util.Counter;
import marmot.util.Numerics;
import marmot.util.edit.EditTree;
import marmot.util.edit.EditTreeBuilder;
import marmot.util.edit.EditTreeBuilderTrainer;

public class EditTreeGeneratorTrainer implements LemmaCandidateGeneratorTrainer {

	private double min_count_;
	private EditTreeGeneratorTrainerOptions options_;
	
	public static class EditTreeGeneratorTrainerOptions extends Options {
		
		private static final String MIN_COUNT = "min-count";

		public EditTreeGeneratorTrainerOptions() {
			map_.put(MIN_COUNT, 1);
		}
		
	}
	
	public EditTreeGeneratorTrainer() {
		options_ = new EditTreeGeneratorTrainerOptions();
	}

	@Override
	public LemmaCandidateGenerator train(List<Instance> instances,
			List<Instance> dev_instances) {
		
		EditTreeBuilder builder = new EditTreeBuilderTrainer(options_.getRandom()).train(instances);
		
		Counter<EditTree> counter = new Counter<>();
		
		for (Instance instance : instances) {
			String form = instance.getForm();
			String lemma = instance.getLemma();
			EditTree tree = builder.build(form, lemma);
			counter.increment(tree, 1.0);
		}
		
		List<EditTree> trees = new LinkedList<>();
		
		for (Map.Entry<EditTree, Double> entry : counter.entrySet()) {
			double count = entry.getValue();
			
			if (Numerics.approximatelyGreaterEqual(count, min_count_)) {
				EditTree tree = entry.getKey();
				trees.add(tree);
			}
		}
		
		return new EditTreeGenerator(trees);
	}

	@Override
	public Options getOptions() {
		return options_;
	}


}