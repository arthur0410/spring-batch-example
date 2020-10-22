package com.example.batchprocessing;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

// tag::setup[]
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Value("${odate:20201023}")
	private String odate;

	private String filePath = "C:\\Users\\nakag\\eclipse-workspace\\gs-batch-processing\\complete\\output\\output.csv";

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	// end::setup[]

	// tag::readerwriterprocessor[]
//	@Bean
//	public FlatFileItemReader<Person> reader() {
//		return new FlatFileItemReaderBuilder<Person>()
//			.name("personItemReader")
//			.resource(new ClassPathResource("sample-data.csv"))
//			.delimited()
//			.names(new String[]{"firstName", "lastName"})
//			.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
//				setTargetType(Person.class);
//			}})
//			.build();
//	}

	@Bean
	public ItemReader<Proposta> reader() {
		return new RESTPropostasReader();
	}

	@Bean
	public PropostaItemProcessor processor(){
		return new PropostaItemProcessor();
	}

//	@Bean
//	public PersonItemProcessor processor() {
//		return new PersonItemProcessor();
//	}

	@Bean
	public JdbcBatchItemWriter<Proposta> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Proposta>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("INSERT INTO propostas (id_proposta) VALUES (:idProposta)")
			.dataSource(dataSource)
			.build();
	}
	// end::readerwriterprocessor[]

	@Bean
	public FlatFileItemWriter<Proposta> writerCsv() {
		//Create writer instance
		FlatFileItemWriter<Proposta> writer = new FlatFileItemWriter<>();

		//Set output file location
		Resource outputResource = new FileSystemResource(filePath + "_" + odate);
		writer.setResource(outputResource);

		//All job repetitions should "append" to same output file
		writer.setAppendAllowed(true);

		//Name field values sequence based on object properties
		writer.setLineAggregator(new DelimitedLineAggregator<Proposta>() {
			{
				setDelimiter(",");
				setFieldExtractor(new BeanWrapperFieldExtractor<Proposta>() {
					{
						setNames(new String[] { "idProposta" });
					}
				});
			}
		});
		return writer;
	}

		// tag::jobstep[]
	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("importUserJob")
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.flow(step1)
			.end()
			.build();
	}

	@Bean
	public Step step1(JdbcBatchItemWriter<Proposta> writer) {
		return stepBuilderFactory.get("step1")
			.<Proposta, Proposta> chunk(10)
			.reader(reader())
//			.processor(processor())
//			.writer(writer)
				.writer(writerCsv())
			.build();
	}
	// end::jobstep[]
}
