package NeuralNetworks;

import java.util.Arrays;

public class Network {
	private double[][] output;	   // [layer] [neuron]
	private double[][][] weights;  // [layer] [neuron] [prevNeuron]
	private double[][] bias;       // [layer] [neuron]
	
	private double[][] error_signal;  // 
	private double[][] output_derivative;
	
	public final int[] NETWORK_LAYER_SIZES;
	public final int INPUT_SIZE;
	public final int OUTPUT_SIZE;
	public final int NETWORK_SIZE;
	
	public Network(int... NETWORK_LAYER_SIZES) {
		this.NETWORK_LAYER_SIZES = NETWORK_LAYER_SIZES;
		this.INPUT_SIZE = NETWORK_LAYER_SIZES[0];
		this.NETWORK_SIZE = NETWORK_LAYER_SIZES.length;
		this.OUTPUT_SIZE = NETWORK_LAYER_SIZES[NETWORK_SIZE - 1];
		
		this.output = new double[NETWORK_SIZE][];
		this.weights = new double[NETWORK_SIZE][][];
		this.bias = new double[NETWORK_SIZE][];
		
		this.error_signal = new double[NETWORK_SIZE][];
		this.output_derivative = new double[NETWORK_SIZE][];
		
		for(int i = 0; i < NETWORK_SIZE; i++) {
			this.output[i] = new double[NETWORK_LAYER_SIZES[i]];	
			this.error_signal[i] = new double[NETWORK_LAYER_SIZES[i]];
			this.output_derivative[i] = new double[NETWORK_LAYER_SIZES[i]];
			
			this.bias[i] = NetworkTools.createRandomArray(NETWORK_LAYER_SIZES[i], 0.3, 0.7);
			
			if(i > 0) {		
				weights[i] = NetworkTools.createRandomArray(NETWORK_LAYER_SIZES[i], NETWORK_LAYER_SIZES[i - 1], -0.3, 0.5);
			}
		}
	}
	
	public double[] calculate(double... input) {
		if(input.length != this.INPUT_SIZE)
			return null;
		this.output[0] = input;
		for(int layer = 1; layer < NETWORK_SIZE; layer++) {
			for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
				
				double sum = bias[layer][neuron];
				for(int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer - 1]; prevNeuron++) {
					sum += output[layer - 1][prevNeuron] * weights[layer][neuron][prevNeuron];
				}
				
				output[layer][neuron] = sigmoid(sum);
				output_derivative[layer][neuron] = output[layer][neuron] * (1 - output[layer][neuron]);
			}
		}
		return output[NETWORK_SIZE - 1];
	}
	
	
	public void train(TrainSet ts, int loops, int batch_size) {
		for(int i = 0; i < loops; i++) {
			TrainSet batch = ts.extractBatch(batch_size);
			for(int j = 0; j < batch_size; j++) {
				this.train(batch.getInput(j), batch.getOutput(j), 0.3);
			}
		}
	}
	
	// training method
	public void train(double[] input, double[] target, double eta) {
		if(input.length != INPUT_SIZE || target.length != OUTPUT_SIZE)
			return;
		calculate(input);
		backpropError(target);
		updateWeights(eta);
	}
	
	// backproError
	public void backpropError(double[] target) {
		for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[NETWORK_SIZE - 1]; neuron++) {
			this.error_signal[NETWORK_SIZE - 1][neuron] = (output[NETWORK_SIZE - 1][neuron] - target[neuron])
															* output_derivative[NETWORK_SIZE - 1][neuron];
		}
		for(int layer = NETWORK_SIZE - 2; layer > 0; layer--) {
			for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
				double sum = 0;
				for(int nextNeuron = 0; nextNeuron < NETWORK_LAYER_SIZES[layer + 1]; nextNeuron++) {
					sum += weights[layer + 1][nextNeuron][neuron] * error_signal[layer + 1][nextNeuron];
				}
				this.error_signal[layer][neuron] = sum * output_derivative[layer][neuron];
			}
		}
	}
	
	// method used to update weights
	public void updateWeights(double eta) {
		for(int layer = 1; layer < NETWORK_SIZE; layer++) {
			for(int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
				for(int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer - 1]; prevNeuron++) {
					// weight[layer][neuron][prevNeuron]
					double delta = -eta * output[layer - 1][prevNeuron] * error_signal[layer][neuron];
					weights[layer][neuron][prevNeuron] += delta;
				}
				double delta = -eta * error_signal[layer][neuron];
				bias[layer][neuron] += delta;
			}
		}
	}
	
	
	// define sigmoid function
	private double sigmoid(double x) {
		return 1d / (1 + Math.exp(-x));
	}
	
	public static void main(String[] args) {
		Network net = new Network(2, 2, 2);
//		double[] input = new double[]{1, 1};
//		double[] output = new double[] {1, 0};
//		double[] input1 = new double[]{1, 0};
//		double[] output1 = new double[] {0, 1};
//		
//		for(int i = 0; i < 100000; i++) {
//			net.train(input, output, 0.3);
//			net.train(input1, output1, 0.3);
//		}
//		
//		System.out.println("Inputs [1]  [1] \nOutput: carray: [" + net.calculate(input)[0] + "] \nsum: [" + net.calculate(input)[1] + "]" );
//		System.out.println("Inputs [1]  [0] \nOutput: carray: [" + net.calculate(input1)[0] + "] \nsum: [" + net.calculate(input1)[1] + "]" );
		TrainSet set = new TrainSet(2, 2);
		set.addData(new double[] {1,  0}, new double[] {0,  1});
		set.addData(new double[] {1,  1}, new double[] {1,  0});
		set.addData(new double[] {0,  0}, new double[] {0,  0});
		set.addData(new double[] {0,  1}, new double[] {0,  1});
		
		net.train(set, 1000000, 4);
		
		for(int i = 0; i < 4; i++)
			System.out.println(Arrays.toString(net.calculate(set.getInput(i))));
		
		
		Network net2 = new Network(2, 3, 4, 1);
		TrainSet set2 = new TrainSet(2, 1);
		set2.addData(new double[] {1,  3}, new double[] {4});
		set2.addData(new double[] {2,  3}, new double[] {5});
		set2.addData(new double[] {4,  3}, new double[] {7});
		set2.addData(new double[] {2,  5}, new double[] {7});
		set2.addData(new double[] {4,  4}, new double[] {8});
		set2.addData(new double[] {1,  9}, new double[] {10});
		set2.addData(new double[] {2,  8}, new double[] {10});
		set2.addData(new double[] {0,  3}, new double[] {3});
		set2.addData(new double[] {1,  0}, new double[] {1});
		net2.train(set2, 100000, 9);
		
		System.out.println(Arrays.toString(net2.calculate(3, 3)));

		
		
	}
}
