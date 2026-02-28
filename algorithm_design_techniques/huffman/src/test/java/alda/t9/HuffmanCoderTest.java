package alda.t9;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class HuffmanCoderTest {

    private HuffmanCoder coder = new HuffmanCoder();

    private void nothingStatic(Class<?> c) {
        for (Field f : c.getDeclaredFields()) {
            assertFalse(Modifier.isStatic(f.getModifiers()));
        }
    }

    @Test
    void nothingStatic() {
        nothingStatic(HuffmanCoder.class);
        nothingStatic(EncodedMessage.class);
    }

    private void test(String msg) {
        EncodedMessage<?, ?> encoded = coder.encode(msg);
        String decoded = coder.decode(encoded);
        assertEquals(msg, decoded);
    }

    @Test
    void abcde() {
        test("aabcabbdabbbabcedab");
    }

    @Test
    void loremIpsum() {
        test("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam id magna in ipsum pharetra lacinia eget vitae arcu. Vivamus pellentesque eget ex non efficitur. Etiam porttitor elementum interdum. Suspendisse potenti. Curabitur dignissim nunc et blandit tempor. Donec mattis mauris vel mi semper sagittis. Maecenas mattis scelerisque lectus, a lacinia tellus dapibus at. Sed in urna ultricies, facilisis tellus ac, accumsan nulla. Nulla sagittis, augue eget laoreet tincidunt, leo purus mattis dolor, aliquet pulvinar mi libero ut dolor. Maecenas et mollis purus. Ut nec pellentesque risus, eu maximus metus. Cras sit amet lorem arcu. Sed nec scelerisque turpis. Quisque tempor ante nec libero bibendum cursus. Duis et tortor mauris. Aenean nec tristique augue, et luctus est. Aenean porta pulvinar finibus. Donec non enim blandit, tristique lorem vel, lacinia magna. Suspendisse a elit accumsan, tempus augue ut, auctor massa. Nam et justo convallis, finibus orci eu, cursus diam. Nulla lacus ante, commodo eu nisl sed, hendrerit ultricies nulla. Integer mattis fringilla suscipit. Donec faucibus ut quam eget rutrum. Aenean erat risus, mattis eu elit eu, eleifend semper diam. Sed ut vulputate magna. Proin ullamcorper lectus nec massa fermentum posuere. Ut gravida sollicitudin erat quis dictum. Morbi dui risus, scelerisque vitae porttitor in, egestas nec velit. Ut quis molestie nibh, tincidunt tristique leo. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Praesent luctus, diam laoreet pharetra vehicula, lacus risus efficitur urna, et tincidunt augue mi aliquet odio. Pellentesque a iaculis justo. Sed scelerisque orci vel sollicitudin dictum. Aliquam dapibus erat nulla. Suspendisse in tempus nisl, ac consectetur sem. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Phasellus eget leo auctor, consequat massa eget, ultrices lorem. Vivamus sit amet urna et velit finibus dapibus eget eget lorem. Phasellus interdum, dolor sit amet ultricies molestie, eros eros consequat ligula, a tincidunt tellus est vel turpis. In ullamcorper nisl purus, ut interdum elit rhoncus in. Fusce id pellentesque odio. Cras sed felis volutpat, auctor odio sed, molestie odio. Sed eget leo at tortor volutpat efficitur. Morbi semper hendrerit dui a lacinia. Donec dictum rutrum nisi, id molestie elit. Quisque lobortis mauris quis tellus feugiat tempor. Fusce nulla dolor, suscipit sed porta id, eleifend egestas felis. Proin scelerisque ut nisi a ornare. Maecenas vel facilisis ligula. Vivamus ante sem, tristique ut dolor et, semper sagittis tellus.");
    }

}
